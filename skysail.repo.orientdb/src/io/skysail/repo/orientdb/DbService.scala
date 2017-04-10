package io.skysail.repo.orientdb

import io.skysail.restlet.model.ScalaSkysailApplicationModel
import com.tinkerpop.blueprints.impls.orient.OrientVertex
import scala.util.Try

trait ScalaDbService {
  
  def createWithSuperClass(superClass: String, vertices: String*)
  def register(classes: Class[_]*)
  
  def persist(entity: Any,  applicationModel: ScalaSkysailApplicationModel): Try[OrientVertex]

  def findGraphs[T](entityType: Class[_], sql: String, arg: Map[String,Object]): List[T]

}