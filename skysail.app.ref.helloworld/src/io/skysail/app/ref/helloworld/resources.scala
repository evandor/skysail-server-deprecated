package io.skysail.app.ref.helloworld

import io.skysail.api.doc._
import org.json4s.DefaultFormats
import io.skysail.core.restlet.resources.ListServerResource
import io.skysail.core.restlet.ResourceContextId
import io.skysail.core.restlet.resources.PostEntityServerResource
import io.skysail.queryfilter.filter.Filter
import io.skysail.queryfilter.pagination.Pagination
import io.skysail.core.restlet.responses.ScalaSkysailResponse
import org.restlet.representation.Variant
import org.restlet.resource.Post
import org.restlet.data.Form
import io.skysail.core.restlet.transformations.Transformations
import org.json4s.jackson.JsonMethods._
import org.json4s.native.Serialization
import org.json4s.native.Serialization.{ read, write }
import org.json4s.JsonAST.JObject
import io.skysail.core.restlet.resources.EntityServerResource
import io.skysail.core.restlet.resources.PutEntityServerResource

class HellosResource extends ListServerResource[List[Hello]](classOf[HelloResource]) {
  addToContext(ResourceContextId.LINK_TITLE, "Hellos")
  override def getEntity() = Services.hellosService.find(new Filter(getRequest()), new Pagination(getRequest(), getResponse()))
  override def linkedResourceClasses() = List(classOf[PostHelloResource], classOf[PutHelloResource])
}

class HelloResource extends EntityServerResource[Hello] {
  override def getEntity(): Hello = Services.hellosService.getById(getAttribute("id")).get
  override def eraseEntity() = null
  override def redirectTo() = super.redirectTo(classOf[HellosResource])
}

class PostHelloResource extends PostEntityServerResource[Hello] {
  addToContext(ResourceContextId.LINK_TITLE, "post Hello")
  def createEntityTemplate() = Hello(None, "world")
  def addEntity(entity: Hello): Hello = Services.hellosService.create(entity).get
  override def redirectTo() = super.redirectTo(classOf[HellosResource])
 
}

class PutHelloResource extends PutEntityServerResource[Hello] {
  addToContext(ResourceContextId.LINK_TITLE, "update Hello")
  override def getEntity(): Hello = Services.hellosService.getById(getAttribute("id")).get
  def updateEntity(entity: Hello): Hello = {
    val original = getEntity()
    copyProperties(original, entity)
    entity.id = original.id
    Services.hellosService.update(entity).get
  }
}

