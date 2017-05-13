package io.skysail.doc.swagger.app

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
import org.restlet.Restlet

object SwaggerApplication {
  final val APP_NAME = "notes"
}

@Component(
  immediate = true,
  configurationPolicy = ConfigurationPolicy.OPTIONAL,
  service = Array(classOf[ApplicationProvider]))
class SwaggerApplication extends SkysailApplication(
  SwaggerApplication.APP_NAME,
  new ApiVersion(int2Integer(1))) {

  val restlets = scala.collection.mutable.Map[String, Restlet]();

  setDescription("swagger documentation app")
  getConnectorService().getClientProtocols().add(Protocol.HTTPS)

  @Reference(policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.MULTIPLE)
  def addApplicationProvider(provider: ApplicationProvider) {
    val application = provider.getSkysailApplication()
    if (application == null) {
      return
    }
    val swaggerRestlet = new SwaggerRestlet2(provider.getSkysailApplication())
    restlets.put(getIdentifier(application), swaggerRestlet);
  }

  def removeApplicationProvider(provider: ApplicationProvider): Unit = {
    val application = provider.getSkysailApplication();
    if (application == null) {
      return
    }
    router.detach(restlets.get(getIdentifier(application)).get);
  }

  override def attach() = {
    restlets
      .keys.toList
      .foreach { key => router.attach("/api/"+key, restlets.get(key).get) }
  }

  private def getIdentifier(application: SkysailApplication) = application.getName() + application.apiVersion.getVersionPath()

}