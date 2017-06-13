package io.skysail.repo.orientdb

import com.tinkerpop.blueprints.impls.orient.OrientGraph
import com.tinkerpop.blueprints.impls.orient.OrientVertex
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import com.tinkerpop.blueprints.Vertex
import java.util.function.Consumer
import scala.collection.JavaConverters._
import scala.util._
import io.skysail.core.model._
import io.skysail.core.domain.ScalaEntity

object Persister {
  def getMethodName(prefix: String, key: String): String = {
    //return key + "_="
    return new StringBuilder(prefix).append(key.substring(0, 1).toUpperCase()).append(key.substring(1)).toString();
  }
}
class Persister(db: OrientGraph, applicationModel: ApplicationModel) {

  var log = LoggerFactory.getLogger(classOf[Persister])

  val double = (i: Int) => { i * 2 }

  val afunction = (i: Any) => { this.execute(i) }

  val edgeHandler = new EdgeHandler(afunction, db)

  val mapper = new ObjectMapper()

  protected def execute(entity: Any): OrientVertex = {
    val vertex = determineVertex(entity.asInstanceOf[ScalaEntity[_]])
    try {
      val props = mapper.convertValue(entity, classOf[java.util.Map[String, Object]])
      props.keySet().stream().forEach(setPropertyOrCreateEdge(entity, vertex, props.asScala.toMap))
      return vertex
    } catch {
      case e: Throwable =>
        log.error(e.getMessage(), e)
        throw new RuntimeException("Problem when persisting entity", e)
    }
    null
  }

  def persist(entity: Any): Try[OrientVertex] = {
    return runInTransaction(entity)
  }

  private def runInTransaction(entity: Any): Try[OrientVertex] = {
    try {
      val result = execute(entity)
      db.commit()
      Success(result)
    } catch {
      case e: Throwable =>
        db.rollback()
        Failure(new RuntimeException("Database Problem, rolled back transaction", e))
    } finally {
      db.shutdown()
    }
  }

  private def determineVertex(entity: ScalaEntity[_]): OrientVertex = {
    require(entity != null, "Provided entity is null")
    if (entity.id.isDefined) {
      return db.getVertex(entity.id.get)
    }
    val name = "class:" + entity.getClass().getName().replace(".", "_")
    db.addVertex(name: Any, Array[Object](): _*)
  }

  val plusOne = new java.util.function.Function[Int, Int] {
    override def apply(t: Int): Int = t + 1

    //  override def andThen[V](after:java.util.function.Function[_ >: Int, _ <: V]):
    //    java.util.function.Function[Int, V] = ???
    //
    //  override def compose[V](before:java.util.function.Function[_ >: V, _ <: Int]):
    //    java.util.function.Function[V, Int] = ???
  }

  class AThingClass(entity: Any, vertex: Vertex, properties: Map[String, Object]) extends java.util.function.Consumer[String] {
    override def accept(key: String): Unit = {
      if ("id".equals(key)) {
        return
      }
      if (isProperty(entity, key)) {
        if (properties.get(key) != null && !("class".equals(key))) {
          setProperty(entity, vertex, key)
        }
      } else {
        try {
          edgeHandler.handleEdges(entity, vertex, properties, key)
        } catch {
          case e: Throwable => log.error(e.getMessage(), e)
        }
      }

    }
  }

  protected def setPropertyOrCreateEdge(entity: Any, vertex: Vertex, properties: Map[String, Object]): Consumer[String] = {
    new AThingClass(entity, vertex, properties)
  }

  private def isProperty(entity: Any, key: String): Boolean = {
    //        if (applicationModel == null) {
    //            return !edges.contains(key)
    //        }
    val entityModel = applicationModel.entityModelFor(entity.getClass())
    if (entityModel.isEmpty) {
      return true
    }
    // val relations = entityModel.getRelations() //.asScala
    //    val relationExists = relations.asScala
    //      .map { e => e.getName }
    //      .filter { e => e == key }
    //      .headOption.isDefined

    //    if (relationExists) {
    //      return false
    //    }
    val sem = entityModel.get
    val optionalField = sem.fieldFor( /*entity.getClass().getName() + "|" +*/ key)
    //val field = optionalField.get.asInstanceOf[FieldModel]
    if (optionalField.isEmpty == null) {
      log.warn("could not determine field for id '{}'", /*entity.getClass().getName() + "|" +*/ key)
      return true
    }
    //    if (field.getEntityType() != null) {
    //      return false
    //    }

    return true
  }
  private def setProperty(entity: Any, vertex: Vertex, key: String) = {
    try {
      if (isOfBooleanType(entity, key)) {
        setVertexProperty("is", entity, vertex, key)
      } else {
        setVertexProperty("get", entity, vertex, key)
      }
    } catch {
      case e: Throwable => log.error(e.getMessage(), e)
    }
  }

  private def isOfBooleanType(e: Any, key: String) = e.getClass().getDeclaredField(key).getType().isAssignableFrom(classOf[Boolean])

  private def setVertexProperty(prefix: String, entity: Any, vertex: Vertex, key: String): Unit = {
    val method = entity.getClass().getMethod(Persister.getMethodName(prefix, key));
    val result = method.invoke(entity);
    if (result == null) {
      return
    }
    //        if (result.isInstanceOf[ValueObject]) {
    //            vertex.setProperty(key, ((ValueObject)result).getValue().toString());
    //        } else {

    vertex.setProperty(key, result);
    //        }
  }

}