package io.skysail.repo.orientdb

import org.osgi.service.component.annotations._
import org.osgi.framework.Constants;
import org.slf4j.LoggerFactory
import scala.collection.JavaConverters._
import org.osgi.service.cm.ConfigurationAdmin
import java.io.IOException
import java.util.Hashtable

@Component(immediate = true, configurationPolicy = ConfigurationPolicy.OPTIONAL, configurationPid = Array("db"))
class ScalaDbConfigurations extends DbConfigurationProvider {

  var log = LoggerFactory.getLogger(classOf[ScalaDbConfigurations])

  var dbConfig: ScalaDbConfig = null
  var loggerThread: Thread = null

  @Reference
  var configurationAdmin: ConfigurationAdmin = null

  @Activate
  def activate(config: java.util.Map[String, String]): Unit = {
    if (config.get(Constants.SERVICE_PID) == null) {
      scheduleCreationOfDefaultConfiguration();
      return ;
    }
    dbConfig = new ScalaDbConfig(
        config.getOrDefault("name", "defaultname"),
        config.getOrDefault("driver", ""),
        config.getOrDefault("url", ""),
        config.getOrDefault("username", ""),
        config.getOrDefault("password", "")        
    )
    log.info(s"activating ${this.getClass().getSimpleName()} with config $dbConfig");
  }

  @Deactivate
  def deactivate() {
    // TODO check if this is enough
    // see OSGiAgent
    loggerThread.interrupt();
  }

  def getConfig(): ScalaDbConfig = dbConfig

  def scheduleCreationOfDefaultConfiguration() {
    val runnable = new Runnable() {
      def run() { createDefaultConfigAfterWaiting(5000) }
    }
    loggerThread = new java.lang.Thread(runnable);
    loggerThread.start();
  }

  def createDefaultConfigAfterWaiting(ms: Int): Unit = {
    try {
      Thread.sleep(ms);

      log.warn("no default database configuration was provided; creating a new one...");
      val instancePid = configurationAdmin.createFactoryConfiguration(classOf[ScalaDbConfigurations].getName(), null)
        .getPid();
      val config = configurationAdmin.getConfiguration(instancePid);
      var props = config.getProperties();
      if (props == null) {
        props = new Hashtable[String, Object]();
      }
      props.put("name", "skysailgraph");
      props.put("url", "memory:skysail");
      props.put("username", "admin");
      props.put("password", "admin");
      config.update(props);

    } catch {
      case e: Throwable => log.error(e.getMessage(), e);
    }

  }

}