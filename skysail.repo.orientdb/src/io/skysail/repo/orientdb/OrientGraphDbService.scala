package io.skysail.repo.orientdb

import org.osgi.service.component.annotations._
import io.skysail.api.metrics.NoOpMetricsCollector
import io.skysail.core.app.SkysailApplicationService
import com.tinkerpop.blueprints.impls.orient._
import com.orientechnologies.orient.`object`.db.OObjectDatabaseTx
import org.slf4j.LoggerFactory
import com.orientechnologies.orient.graph.sql._
import com.orientechnologies.orient.graph.sql.functions._

import scala.collection.JavaConverters._
import com.orientechnologies.orient.core.sql._
import org.osgi.service.component._
import com.orientechnologies.orient.core.db.OPartitionedDatabasePool

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

  def registerShutdownHook(): Unit = {
    ???
  }

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
}