package io.skysail.app.ref.helloworld

import org.osgi.service.component.annotations._
import org.restlet.data.Protocol
import java.util.Arrays
import io.skysail.core.security.config.SecurityConfigBuilder
import io.skysail.core.app._
import io.skysail.repo.orientdb.ScalaDbService
import io.skysail.core.ApiVersion
import io.skysail.restlet.RouteBuilder
import io.skysail.core.app.ApplicationConfiguration
import io.skysail.restlet.services.MenuItemProvider
import org.osgi.service.component.ComponentContext
import io.skysail.core.model.APPLICATION_CONTEXT_RESOURCE
import io.skysail.app.ref.helloworld._

object HelloWorldApplication {
  final val APP_NAME = "hello"
}

@Component(
  immediate = true,
  configurationPolicy = ConfigurationPolicy.OPTIONAL,
  service = Array(classOf[ApplicationProvider]))
class HelloWorldApplication extends SkysailApplication(HelloWorldApplication.APP_NAME,new ApiVersion(int2Integer(1))) {

  setDescription("helloworld reference application")
  getConnectorService().getClientProtocols().add(Protocol.HTTPS)

  addAssociatedResourceClasses(List((APPLICATION_CONTEXT_RESOURCE, classOf[HellosResource])))

  var hellosService: HelloWorldService = null

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
    hellosService = new HelloWorldService(dbService, getApplicationModel2())
  }

  override def attach() = {
    router.attach(new RouteBuilder("", classOf[HellosResource]))
    router.attach(new RouteBuilder("/hellos", classOf[HellosResource]))
    router.attach(new RouteBuilder("/hellos/", classOf[PostHelloResource]))
    router.attach(new RouteBuilder("/hellos/{id}", classOf[HelloResource]))
    router.attach(new RouteBuilder("/hellos/{id}/", classOf[PutHelloResource]))

    createStaticDirectory();
  }

  override def defineSecurityConfig(securityConfigBuilder: SecurityConfigBuilder) = {
    securityConfigBuilder.authorizeRequests().startsWithMatcher("").permitAll();
  }

}