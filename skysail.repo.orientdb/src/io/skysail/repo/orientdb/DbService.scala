package io.skysail.repo.orientdb

import io.skysail.restlet.model.ScalaSkysailApplicationModel
import com.tinkerpop.blueprints.impls.orient.OrientVertex
import scala.util.Try
import org.json4s.JsonAST.JValue

trait ScalaDbService {
  
  def createWithSuperClass(superClass: String, vertices: String*)
  def register(classes: Class[_]*)
  
  def persist(entity: Any,  applicationModel: ScalaSkysailApplicationModel): Try[OrientVertex]

  def findGraphs[T](entityType: Class[_], sql: String, arg: Map[String,Object]): List[JValue]

}