package io.skysail.repo.orientdb

import com.tinkerpop.blueprints.impls.orient._
import com.orientechnologies.orient.`object`.db.OObjectDatabaseTx
import com.orientechnologies.orient.graph.sql._
import com.orientechnologies.orient.graph.sql.functions._
import com.orientechnologies.orient.core.sql._
import com.orientechnologies.orient.core.db.OPartitionedDatabasePool
import com.orientechnologies.orient.core.record.impl.ODocument
import io.skysail.api.metrics.NoOpMetricsCollector
import io.skysail.restlet.ScalaSkysailBeanUtils
import io.skysail.restlet.app.SkysailApplicationService
import java.util.Locale
import org.osgi.service.component._
import org.osgi.service.component.annotations._
import org.slf4j.LoggerFactory
import scala.collection.JavaConverters._
import scala.util._
import scala.collection.JavaConverters._
import io.skysail.restlet.transformations.Transformations
import org.json4s.DefaultFormats
import org.json4s.JsonAST.JValue
import io.skysail.core.model.ApplicationModel
import org.json4s.JsonAST.JString

@Component(immediate = true)
class OrientGraphDbService extends AbstractOrientDbService with ScalaDbService {

  override val log = LoggerFactory.getLogger(classOf[OrientGraphDbService])

  var graphDbFactory: OrientGraphFactory = null

  //    def Map<String, Entity> beanCache = new HashMap<>()
  //
  //    def AtomicLong al = new AtomicLong()

  @Reference(cardinality = ReferenceCardinality.OPTIONAL)
  var metricsCollector = new NoOpMetricsCollector()

  @Reference
  var appService: SkysailApplicationService = null

  var db: OObjectDatabaseTx = null

  @Reference(policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.MANDATORY, target = "(name=default)")
  def setDbConfigurationProvider(provider: DbConfigurationProvider) {
    updated(provider)
  }

  def unsetDbConfigurationProvider(provider: DbConfigurationProvider) {}

  @Activate
  def activate(): Unit = {
    log.debug("activating {}", this.getClass().getName())
    //http://stackoverflow.com/questions/30291359/orient-db-unable-to-open-any-kind-of-graph
    if (getDbUrl().startsWith("memory:")) {
      // https://github.com/orientechnologies/orientdb/issues/5105
      // com.orientechnologies.common.util.OClassLoaderHelper
      new OGraphCommandExecutorSQLFactory()
      new OrientGraphNoTx(getDbUrl())
    }

    graphDbFactory = new OrientGraphFactory(getDbUrl(), getDbUsername(), getDbPassword()).setupPool(1, 10)

    val graphFunctions = new OGraphFunctionFactory()
    val names = graphFunctions.getFunctionNames()

    for (name <- names.asScala) {
      OSQLEngine.getInstance().registerFunction(name, graphFunctions.createFunction(name))
      val function = OSQLEngine.getInstance().getFunction(name)
      if (function != null) {
        log.debug(s"ODB graph function [$name] is registered: [$function.getSyntax()]")
      } else {
        log.warn("ODB graph function [$name] NOT registered!!!")
      }
    }
  }

  @Deactivate
  def deactivate(context: ComponentContext): Unit = {
    log.debug("activating {}", this.getClass().getName())
    stopDb()
    graphDbFactory = null
  }

  def createWithSuperClass(superClass: String, vertices: String*): Unit = {
    val objectDb = getObjectDb()
    try {
      vertices.foreach(v => {
        if (objectDb.getMetadata().getSchema().getClass(v) == null) {
          val vertexClass = objectDb.getMetadata().getSchema().getClass(superClass)
          objectDb.getMetadata().getSchema().createClass(v).setSuperClass(vertexClass)
        }
      })
    } catch {
      case e: Throwable => log.error(e.getMessage(), e)
    }
  }

  def registerShutdownHook(): Unit = Runtime.getRuntime().addShutdownHook(new Thread() { override def run() = stopDb() })

  def startDb(): Unit = {
    if (started) {
      return ;
    }
    try {
      log.debug("about to start db");

      createDbIfNeeded();

      val opDatabasePool = new OPartitionedDatabasePool(getDbUrl(), getDbUsername(), getDbPassword());
      val oDatabaseDocumentTx = opDatabasePool.acquire();
      db = new OObjectDatabaseTx(oDatabaseDocumentTx);

      log.debug("setting lazy loading to false");
      db.setLazyLoading(false);
      started = true;
      if (getDbUrl().startsWith("memory:")) {
        // remark: this might be called without an eventhandler already
        // listening and so the event might be lost
        //        EventHandler.sendEvent(EventHelper.GUI_MSG,
        //          "In-Memory database is being used, all data will be lost when application is shut down",
        //          "warning");
      }
    } catch {
      case e: Throwable => log.error(e.getMessage(), e);
    }

  }

  def stopDb(): Unit = {
    started = false;
    graphDbFactory.close();
    if (getDbUrl().startsWith("memory")) {
      try {
        db.drop();
      } catch {
        case e: Throwable => log.error(e.getMessage(), e);
      }
    }

  }

  def register(entities: Class[_]*): Unit = {
    val db = getObjectDb()
    try {
      entities.foreach(entity => {
        log.debug("registering class '{}' @ orientDB", entity)
        db.getEntityManager().registerEntityClass(entity)
      })
    } finally {
      db.close()
    }

  }

  def getObjectDb() = {
    val opDatabasePool = new OPartitionedDatabasePool(getDbUrl(), getDbUsername(), getDbPassword())
    new OObjectDatabaseTx(opDatabasePool.acquire())
  }

  def createDbIfNeeded() = {
    val dbUrl = getDbUrl();
    if (dbUrl.startsWith("remote")) {
      log.debug("registering remote engine");
      //Orient.instance().getEngines().a
      //Orient.instance().registerEngine(new OEngineRemote());
    } else if (dbUrl.startsWith("plocal")) {

      val graph = new OrientGraph(dbUrl, getDbUsername(), getDbPassword());
      try {
        log.debug("testing graph factory connection");
      } catch {
        case e: Throwable => log.error(e.getMessage(), e);
      } finally {
        graph.shutdown();
      }
    } else if (dbUrl.startsWith("memory:")) {
      val databaseTx = new OObjectDatabaseTx(dbUrl);
      if (!databaseTx.exists()) {
        val create = databaseTx.create();
        log.debug("created new in-memory database {}", create.toString());
      }

      val factory = new OrientGraphFactory(dbUrl, getDbUsername(), getDbPassword()).setupPool(1, 10);
      try {
        log.debug("testing graph factory connection");
        factory.getTx();
      } catch {
        case e: Throwable => log.error(e.getMessage(), e);
      } finally {
        factory.close();
      }
    }

  }

  def persist(entity: Any, applicationModel: ApplicationModel): Try[OrientVertex] = {
    new Persister(getGraphDb(), applicationModel).persist(entity)
  }

  def findGraphs[T](cls: Class[_], sql: String, params: Map[String, Object]): List[JValue] = {
    //println(metricsCollector)
    //val timer = getMetricsCollector().timerFor(this.getClass(), "findGraphs");
    val graph = getGraphDb();
    val oCommand = new OCommandSQL(sql);
    val commandRequest = graph.command(oCommand)
    val execute = commandRequest.execute[OrientDynaElementIterable](params.asJava)

    val result = scala.collection.mutable.ListBuffer[JValue]()
    val iterator = execute.iterator();
    //    //beanCache.clear();
    while (iterator.hasNext()) {
      val next = iterator.next().asInstanceOf[OrientVertex]
      val optionOfBean = documentToBean(next.getRecord(), cls)
      //if (optionOfBean.isDefined) {
          result += optionOfBean//.get
      //}
    }
    //timer.stop();
    result.toList
  }

  private def getGraphDb(): OrientGraph = graphDbFactory.getTx()

  private def documentToBean[T:Manifest](document: ODocument, beanType: Class[_]): JValue = {
    val documentAsScalaMap = document.toMap().asScala.toMap
    val id = document.getIdentity.toString().replace("#","")
    Transformations.jsonFrom[T](documentAsScalaMap.+ ("id" -> id))
  }

  private def populateProperties[T](entityMap: Map[String, Object], bean: T, beanUtilsBean: ScalaSkysailBeanUtils[T]): Unit = {
    beanUtilsBean.populate(bean, entityMap);
    //        if (entityMap.get("@rid") != null && bean.getId() == null) {
    //            Field field;
    //            try {
    //                field = bean.getClass().getDeclaredField("id");
    //                field.setAccessible(true);
    //                field.set(bean, entityMap.get("@rid").toString());
    //            } catch (NoSuchFieldException | SecurityException e) {
    //                log.error(e.getMessage(),e);
    //            }
    //        }
  }

  private def populateOutgoingEdges[T](document: ODocument, bean: T) = {
    val outFields = getOutgoingFieldNames(document)
    //        outFields.forEach(edgeName -> {
    //            ORidBag field = document.field(edgeName);
    //            field.setAutoConvertToRecord(true);
    //            field.convertLinks2Records();
    //
    //            ORidBag edgeIdBag = document.field(edgeName);
    //            Iterator<OIdentifiable> iterator = edgeIdBag.iterator();
    //            List<Entity> identifiables = new ArrayList<>();
    //            while (iterator.hasNext()) {
    //                ODocument edge = (ODocument) iterator.next();
    //                if (edge == null) {
    //                    continue;
    //                }
    //                ODocument inDocumentFromEdge = edge.field("in");
    //                String targetClassName = inDocumentFromEdge.getClassName().substring(
    //                        inDocumentFromEdge.getClassName().lastIndexOf("_") + 1);
    //                Class<?> targetClass = getObjectDb().getEntityManager().getEntityClass(targetClassName);
    //                Entity identifiable = beanCache.get(inDocumentFromEdge.getIdentity().toString());
    //                if (identifiable != null) {
    //                    identifiables.add(identifiable);
    //                } else {
    //                    identifiables.add(documentToBean(inDocumentFromEdge, targetClass));
    //                }
    //            }
    //            String fieldName = edgeName.replace("out_", "");
    //            String setterName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    //            try {
    //                bean.getClass().getMethod(setterName, List.class).invoke(bean, identifiables);
    //            } catch (Exception e) {
    //                log.error(e.getMessage(), e);
    //
    //            }
    //        });
  }

  def getOutgoingFieldNames(document: ODocument) = document.fieldNames().filter { f => f.startsWith("out_") }.toList
}