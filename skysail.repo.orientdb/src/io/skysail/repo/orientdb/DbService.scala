package io.skysail.repo.orientdb

import io.skysail.restlet.model.ScalaSkysailApplicationModel
import com.tinkerpop.blueprints.impls.orient.OrientVertex

trait ScalaDbService {
  
  def createWithSuperClass(superClass: String, vertices: String*)
  def register(classes: Class[_]*)
  
  def persist(entity: Any,  applicationModel: ScalaSkysailApplicationModel): OrientVertex

}