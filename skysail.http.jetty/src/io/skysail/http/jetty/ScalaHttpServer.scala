package io.skysail.http.jetty

import io.skysail.restlet.ScalaSkysailComponent
import io.skysail.restlet.services._
import java.util.logging.Level
import java.net.ServerSocket
import java.io.IOException
import org.osgi.service.component.annotations._
import org.osgi.service.component._
import org.restlet.resource.ServerResource
import org.restlet.ext.jackson.JacksonConverter
import org.restlet.service.ConverterService
import org.restlet.engine.Engine
import org.slf4j.LoggerFactory
import org.restlet.Server
import org.restlet.Context
import org.restlet.data.Protocol
import org.eclipse.jetty.servlet.ServletContextHandler;
import scala.collection.JavaConverters._

object ScalaHttpServer {

  var log = LoggerFactory.getLogger(this.getClass())
  var server: Server = null
  var serverActive = false
  var restletComponent = new ScalaSkysailComponent()

  private def startHttpServer(port: Int) = {
    log.info("");
    log.info("====================================");
    log.info("Starting skysail server on port {}", port);
    log.info("====================================");
    log.info("");

    if (server == null) {
      try {
        server = new Server(new Context(), Protocol.HTTP, Integer.valueOf(port), restletComponent);
        server.getContext().getParameters().add("useForwardedForHeader", "true");

        val context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/jetty");
        //context.addServlet(EchoServlet.class.getName(), "/echo");
        server.start();
      } catch {
        case e: Throwable => log.error("Exception when starting standalone server trying to parse provided port (" + port + ")", e);
      }
    } else {
      try {
        server.start();
      } catch {
        case e: Throwable => e.printStackTrace();
      }
    }
    if (server == null) {
      throw new ComponentException("skysail server could not be started");
    }
    serverActive = true;
  }
}

@Component(
  immediate = true,
  configurationPolicy = ConfigurationPolicy.OPTIONAL,
  configurationPid = Array("skysailserver"),
  property = { Array("event.topics=de/twenty11/skysail/server/configuration/UPDATED") })
class ScalaHttpServer extends ServerResource
    with ScalaRestletServicesProvider with ScalaSkysailComponentProvider with ScalaInstallationProvider {

  var log = LoggerFactory.getLogger(this.getClass)

  var defaultApplication: Any = null //SkysailRootApplication
  var componentContext: ComponentContext = null
  var productName = "skysail"
  var runningOnPort = 2015

  Engine.setRestletLogLevel(Level.ALL)
  removeDefaultJacksonConverter()
  var registeredConverters = Engine.getInstance().getRegisteredConverters()

  def getPort(): Int = runningOnPort

  @Activate
  def activate(serverConfig: ServerConfig, componentContext: ComponentContext) {
    log.debug(s"Activating ${this.getClass().getName()}");
    this.componentContext = componentContext;
    if (ScalaHttpServer.restletComponent == null) {
      ScalaHttpServer.restletComponent = new ScalaSkysailComponent();
    }

    configure(serverConfig);

    log.debug("Started with system properties:");
    log.debug("===============================");
    //        System.getProperties().entrySet().stream()
    //                .sorted((e1, e2) -> e1.getKey().toString().compareTo(e2.getKey().toString())).forEach(entry -> {
    //                    Object value = entry.getValue();
    //                    log.debug("  {} => '{}'", entry.getKey(), value == null ? "<null>" : value.toString());
    //                });
  }

  @Deactivate
  def deactivate(ctxt: ComponentContext) {
    log.debug("Deactivating {}", this.getClass().getName());

    ScalaHttpServer.serverActive = false;
    try {
      if (ScalaHttpServer.server != null) {
        ScalaHttpServer.server.stop();
      }
    } catch {
      case e: Throwable => log.error("Exception when trying to stop standalone server", e);
    }

    //    restletComponent.getDefaultHost().detach(defaultApplication);

    ScalaHttpServer.restletComponent.setServers(null);

    try {
      ScalaHttpServer.restletComponent.stop();
    } catch {
      case e: Throwable => log.error(e.getMessage(), e);
    }

    this.componentContext = null;
  }

  private def removeDefaultJacksonConverter() = {
    val converters = Engine.getInstance().getRegisteredConverters().asScala
      .filter(converter => !(converter.isInstanceOf[JacksonConverter])).toList
    Engine.getInstance().setRegisteredConverters(converters.asJava);
  }

  def getConverterSerivce(): ConverterService = null //defaultApplication.getConverterService()

  def getProductName(): String = productName

  def getSkysailComponent(): ScalaSkysailComponent = ScalaHttpServer.restletComponent

  private def configure(serverConfig: ServerConfig) = {
    log.debug("configuration was provided");
    runningOnPort = serverConfig.port();
    productName = serverConfig.productName();
    if (!ScalaHttpServer.serverActive) {
      if (serverConfig.port() == 0) {
        runningOnPort = findAvailablePort();
      }
      ScalaHttpServer.startHttpServer(runningOnPort);
    }
  }

  private def findAvailablePort(): Int = {
    var socket: ServerSocket = null;
    try {
      socket = new ServerSocket(0);
      return socket.getLocalPort();
    } catch {
      case e: IOException => log.error(e.getMessage(), e)
    } finally {
      if (socket != null) {
        try {
          socket.close();
        } catch {
          case _: Throwable =>
        }
      }
    }
    2015
  }

}