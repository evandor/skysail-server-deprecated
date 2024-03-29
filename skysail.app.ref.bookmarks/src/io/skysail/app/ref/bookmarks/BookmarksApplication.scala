package io.skysail.app.ref.bookmarks

import org.osgi.service.component.annotations._
import org.restlet.data.Protocol
import java.util.Arrays
import io.skysail.core.security.config.SecurityConfigBuilder
import io.skysail.core.app._
import io.skysail.repo.orientdb.ScalaDbService
import io.skysail.core.ApiVersion
import io.skysail.core.restlet.RouteBuilder
import io.skysail.core.app.ApplicationConfiguration
import io.skysail.core.restlet.services.MenuItemProvider
import org.osgi.service.component.ComponentContext
import io.skysail.core.model.APPLICATION_CONTEXT_RESOURCE
import io.skysail.app.ref.bookmarks.services.BookmarksService
import io.skysail.app.ref.bookmarks.resources.BookmarksResource
import io.skysail.app.ref.bookmarks.resources.PostBookmarkResource
import io.skysail.app.ref.bookmarks.resources.BookmarkResource
import io.skysail.app.ref.bookmarks.resources.PutBookmarkResource

object BookmarksApplication {
  final val APP_NAME = "bookmarks"
}

@Component(
  immediate = true,
  configurationPolicy = ConfigurationPolicy.OPTIONAL,
  service = Array(classOf[ApplicationProvider]))
class BookmarksApplication extends SkysailApplication(BookmarksApplication.APP_NAME,new ApiVersion(int2Integer(1))) {

  setDescription("bookmarks reference application")
  getConnectorService().getClientProtocols().add(Protocol.HTTPS)

  addAssociatedResourceClasses(List((APPLICATION_CONTEXT_RESOURCE, classOf[BookmarksResource])))

  var bookmarksService: BookmarksService = null

  @Reference(cardinality = ReferenceCardinality.MANDATORY)
  var dbService: ScalaDbService = null

  @Reference(policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.OPTIONAL)
  def setApplicationListProvider(service: ScalaServiceListProvider) {
    SkysailApplication.serviceListProvider = service;
  }

  def unsetApplicationListProvider(service: ScalaServiceListProvider) {
    SkysailApplication.serviceListProvider = null;
  }

  @Activate
  override def activate(appConfig: ApplicationConfiguration, componentContext: ComponentContext) = {
    super.activate(appConfig, componentContext);
    bookmarksService = new BookmarksService(dbService, getApplicationModel())
  }

  override def attach() = {
    router.attach(new RouteBuilder("", classOf[BookmarksResource]))
    router.attach(new RouteBuilder("/bookmarks", classOf[BookmarksResource]))
    router.attach(new RouteBuilder("/bookmarks/", classOf[PostBookmarkResource]))
    router.attach(new RouteBuilder("/bookmarks/{id}", classOf[BookmarkResource]))
    router.attach(new RouteBuilder("/bookmarks/{id}/", classOf[PutBookmarkResource]))

    createStaticDirectory();
  }

  override def defineSecurityConfig(securityConfigBuilder: SecurityConfigBuilder) = {
    securityConfigBuilder.authorizeRequests().startsWithMatcher("").permitAll();
  }

}