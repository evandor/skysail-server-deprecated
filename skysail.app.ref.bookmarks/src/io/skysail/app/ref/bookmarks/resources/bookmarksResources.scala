package io.skysail.app.ref.bookmarks.resources

import io.skysail.api.doc._
import org.json4s.DefaultFormats
import io.skysail.restlet.resources.ListServerResource
import io.skysail.restlet.ResourceContextId
import io.skysail.restlet.resources.PostEntityServerResource
import io.skysail.queryfilter.filter.Filter
import io.skysail.queryfilter.pagination.Pagination
import io.skysail.restlet.responses.ScalaSkysailResponse
import org.restlet.representation.Variant
import org.restlet.resource.Post
import org.restlet.data.Form
import io.skysail.restlet.transformations.Transformations
import org.json4s.jackson.JsonMethods._
import org.json4s.native.Serialization
import org.json4s.native.Serialization.{ read, write }
import org.json4s.JsonAST.JObject
import io.skysail.app.ref.bookmarks.domain.Bookmark
import io.skysail.app.ref.bookmarks.services.Services
import io.skysail.restlet.resources.EntityServerResource
import io.skysail.restlet.resources.PutEntityServerResource

class BookmarksResource extends ListServerResource[List[Bookmark]](classOf[BookmarkResource]) {
  addToContext(ResourceContextId.LINK_TITLE, "bookmarks")
  override def getEntity() = Services.bookmarks.find(new Filter(getRequest()), new Pagination(getRequest(), getResponse()))
  override def linkedResourceClasses() = List(classOf[PostBookmarkResource], classOf[PutBookmarkResource])
}

class BookmarkResource extends EntityServerResource[Bookmark] {
  override def getEntity(): Bookmark = Services.bookmarks.getById(getAttribute("id")).get
  override def eraseEntity() = null
  override def redirectTo() = super.redirectTo(classOf[BookmarksResource])
}

class PostBookmarkResource extends PostEntityServerResource[Bookmark] {
  addToContext(ResourceContextId.LINK_TITLE, "post bookmark")
  def createEntityTemplate() = Bookmark(None, "title", "url")
  def addEntity(entity: Bookmark): Bookmark = Services.bookmarks.create(entity).get
}

class PutBookmarkResource extends PutEntityServerResource[Bookmark] {
  addToContext(ResourceContextId.LINK_TITLE, "update bookmark")
  override def getEntity(): Bookmark = Services.bookmarks.getById(getAttribute("id")).get
  def updateEntity(entity: Bookmark): Unit = {
    val original = getEntity()
    //    val originalCreated = original.getCreated()
    copyProperties(original, entity)
    //    original.setCreated(originalCreated)
    //    original.setModified(new Date())
    //    //NotesResource.noteRepo(getApplication()).update(original, getApplicationModel())
    Services.bookmarks.update(entity)
  }
}

