package io.skysail.ui.raw

import org.osgi.service.cm.ConfigurationException
import org.osgi.service.component.ComponentContext
import org.osgi.service.component.annotations._
import io.skysail.core.app._
import io.skysail.core.app._
import io.skysail.core.ApiVersion
import io.skysail.core.security.config.SecurityConfigBuilder

object RawUiApplication {
  final val APP_NAME = "_ui/" + RawTemplatesProvider.NAMESPACE;
}

@Component(
  immediate = true,
  configurationPolicy = ConfigurationPolicy.OPTIONAL,
  service = Array(classOf[ApplicationProvider]))
class RawUiApplication extends SkysailApplication(RawUiApplication.APP_NAME, new ApiVersion(1)) {

  setDescription("UI provider for " + RawTemplatesProvider.NAMESPACE);

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
  }

  override def defineSecurityConfig(securityConfigBuilder: SecurityConfigBuilder) = {
    securityConfigBuilder.authorizeRequests().startsWithMatcher("").authenticated();
  }
}