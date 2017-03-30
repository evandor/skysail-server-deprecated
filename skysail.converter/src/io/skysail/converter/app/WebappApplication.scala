package io.skysail.converter.app

import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.restlet.data.Protocol;
import io.skysail.core.app.SkysailApplication
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import io.skysail.core.app.ApplicationProvider
import io.skysail.server.menus.MenuItemProvider
import io.skysail.core.app.ApiVersion
import org.osgi.service.component.annotations.Activate
import org.osgi.service.component.ComponentContext
import io.skysail.core.app.ApplicationConfiguration
import org.restlet.data.Protocol
import io.skysail.server.restlet.RouteBuilder
import io.skysail.server.menus.MenuItem
import java.util.Arrays
import org.restlet.Restlet
import org.restlet.data.LocalReference
import io.skysail.core.utils.CompositeClassLoader
import org.restlet.routing.Router
import io.skysail.core.utils.ClassLoaderDirectory
import org.restlet.routing.Filter
import org.restlet.Request
import org.restlet.Response
import org.restlet.data.Status
import java.util.GregorianCalendar
import java.util.Date
import java.util.Calendar
import org.restlet.data.CacheDirective

object WebappApplication {
  final val APP_NAME = "webapp"
}

@Component(
  immediate = true,
  configurationPolicy = ConfigurationPolicy.OPTIONAL,
  service = Array(classOf[ApplicationProvider], classOf[MenuItemProvider]))
class WebappApplication extends SkysailApplication(
  WebappApplication.APP_NAME,
  new ApiVersion(int2Integer(1))) with MenuItemProvider {

  setDescription("webapp serving static files")
  getConnectorService().getClientProtocols().add(Protocol.HTTPS)

  override def createInboundRoot(): Restlet = {

    val localReference = LocalReference.createClapReference(LocalReference.CLAP_THREAD, "/webapp/")

    val customCL = new CompositeClassLoader()
    customCL.addClassLoader(Thread.currentThread().getContextClassLoader())
    customCL.addClassLoader(classOf[Router].getClassLoader())
    customCL.addClassLoader(this.getClass().getClassLoader())

    val staticDirectory = new ClassLoaderDirectory(getContext(), localReference, customCL);

    val cachingFilter = new Filter() {
      override def getNext(): Restlet = staticDirectory

      override def afterHandle(request: Request, response: Response) = {
        super.afterHandle(request, response);
        if (response.getEntity() != null) {
          if (request.getResourceRef().toString(false, false).contains("nocache")) {
            response.getEntity().setModificationDate(null);
            response.getEntity().setExpirationDate(null);
            response.getEntity().setTag(null);
            response.getCacheDirectives().add(CacheDirective.noCache());
          } else {
            response.setStatus(Status.SUCCESS_OK);
            val c = new GregorianCalendar();
            c.setTime(new Date());
            c.add(Calendar.DAY_OF_MONTH, 10);
            response.getEntity().setExpirationDate(c.getTime());
            response.getEntity().setModificationDate(null);
            response.getCacheDirectives().add(CacheDirective.publicInfo());
          }
        }
      }
    };

    val router = new Router(getContext());
    router.attachDefault(cachingFilter);
    return router;
  }

  override def getApplication() = this

}