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

class BookmarksResource extends ListServerResource[List[Bookmark]]() {

  setDescription("""resource dealing with retrieving all configured Bookmarks""")

  addToContext(ResourceContextId.LINK_TITLE, "wyt")

  @ApiSummary("list of all Bookmarks")
  override def getEntity() = {
    Services.bookmarks.find(new Filter(getRequest()), new Pagination(getRequest(), getResponse()))
    //associatedResourceClasses(Array(classOf[TurnResource]))
  }
  override def linkedResourceClasses() = List(
    classOf[PostBookmarkResource]
  )

}

class PostBookmarkResource extends PostEntityServerResource[Bookmark] {

  setDescription("""resource dealing with posting Bookmarks""")
  addToContext(ResourceContextId.LINK_TITLE, "post Bookmark")

  def createEntityTemplate() = Bookmark("title", "url")

  @ApiSummary("returns a Bookmark template")
  def addEntity(entity: Bookmark): Bookmark = {
    //Services.turns.confirm(entity)
    val Bookmark = Services.bookmarks.create(entity)
    //Bookmark(Some("1"), "default Bookmark")
    Bookmark.get
  }

}


