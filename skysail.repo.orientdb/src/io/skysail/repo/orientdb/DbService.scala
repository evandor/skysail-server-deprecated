package io.skysail.repo.orientdb

import com.tinkerpop.blueprints.impls.orient.OrientVertex
import scala.util.Try
import org.json4s.JsonAST.JValue
import io.skysail.core.model.ApplicationModel

trait ScalaDbService {
  
  def createWithSuperClass(superClass: String, vertices: String*)
  def register(classes: Class[_]*)
  
  def persist(entity: Any,  applicationModel: ApplicationModel): Try[OrientVertex]

  def findGraphs[T](entityType: Class[_], sql: String, arg: Map[String,Object]): List[JValue]

}