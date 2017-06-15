package io.skysail.config.init

import aQute.bnd.annotation.component._
import ch.qos.logback.classic._
import io.skysail.core.Constants
import java.io.IOException
import java.nio.file._
import org.osgi.service.component.ComponentContext
import org.slf4j.LoggerFactory
import org.osgi.framework.Bundle
import scala.collection.JavaConverters._
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * The ConfigMover, if included in a skysail installation, will try to copy the
 * configuration files from the current product bundle (defined by the system
 * property "product.bundle") into the installations config folder (target
 * folder).
 *
 * The product bundle has to contain a directory ./config/standalone, which
 * contains all the files to be copied.
 *
 * The target folder will be created if it doesn't exist. Only files which don't
 * exist in the target folder will be copied.
 *
 */

object ConfigMover {

  private val log = LoggerFactory.getLogger(this.getClass())

  /** comma-separated list of subdirectories of config dir to be copied. */
  private final val CONFIG_PATH_SOURCES = "config"

  def readResource(bundle: Bundle, path: String): String = {
    log.info(s"about to read Resource '$path'")
    val url = bundle.getResource(path)
    val sb = new StringBuilder()
    try {
      val br = new BufferedReader(new InputStreamReader(url.openConnection().getInputStream()))
      while (br.ready()) {
        sb.append(br.readLine()).append("\n")
      }
      br.close()
    } catch {
      case e: Throwable => log.error(e.getMessage(), e)
    }
    return sb.toString()
  }
}

@Component(immediate = true, configurationPolicy = ConfigurationPolicy.optional)
class ConfigMover {

  private val log = LoggerFactory.getLogger(this.getClass())

  /** set to true, if a logback config file was discovered during copying. */
  var logbackConfigurationExists = false

  @Activate
  def activate(context: ComponentContext) = {
    val configCreated = copyConfigurationFromProductJar(context)
    if (!logbackConfigurationExists) {
      log.info("setting logging level to INFO, no initial logback confi file was provided yet.")
      val root = org.slf4j.LoggerFactory.getLogger("ROOT").asInstanceOf[Logger]
      root.setLevel(Level.INFO)
    }
    if (configCreated) {
      log.warn("")
      log.warn("====================================================================")
      log.warn("### skysail did not find any (or all) configuration files...     ###")
      log.warn("###                                                              ###")
      log.warn("### A default configuration was created, please restart skysail. ###")
      log.warn("###                                                              ###")
      log.warn("### The framework is shutting itself down now...                 ###")
      log.warn("====================================================================")
      log.warn("")
      try {
        context.getBundleContext().getBundle(0).stop()
      } catch {
        case e: Throwable => log.error(e.getMessage(), e)
      }
    }
  }

  private def copyConfigurationFromProductJar(context: ComponentContext): Boolean = {
    val productBundleName = System.getProperty(Constants.PRODUCT_BUNDLE_IDENTIFIER)
    log.debug("determined product bundle to be '{}'", productBundleName)
    val productBundle = context.getBundleContext().getBundles()
      .filter { b => b.getSymbolicName().equals(productBundleName) }
      .headOption
    copyConfigurationFilesOrWarn(productBundle)
  }

  private def copyConfigurationFilesOrWarn(productBundle: Option[Bundle]): Boolean = {
    if (productBundle.isDefined) {
      return copyConfigurationFiles(productBundle.get)
    } else {
      log.warn("could not determine product bundle, no default configuration was copied")
      return false
    }
  }

  private def copyConfigurationFiles(bundle: Bundle): Boolean = {
    var configCreated = false
    val fromPaths = getFrom(bundle)
    log.info("copyConfigurationFiles, found paths: {}", fromPaths)
    for (fromPath <- fromPaths) {
      val standalonePath = fromPath + "/standalone"
      log.info("checking path {}", standalonePath)
      val entryPaths = bundle.getEntryPaths(standalonePath).asScala
      if (entryPaths == null) {
        log.info(s"no configuration found in bundle ${bundle.getSymbolicName()} at path ${standalonePath}")
      }
      val copyToPath = Paths.get("./" + fromPath)
      try {
        log.info("will try to create directory '{}' if it not exists", copyToPath.toAbsolutePath().toString())
        Files.createDirectories(copyToPath)
        configCreated = handleConfigFiles(bundle, entryPaths) || configCreated
      } catch {
        case e: FileAlreadyExistsException => log.info("file '{}' already exists, no config files will be copied",
          copyToPath.toAbsolutePath().toString())
        case e1: Throwable => log.error(e1.getMessage(), e1)
      }
    }
    return configCreated
  }

  private def getFrom(bundle: Bundle): List[String] = {
    log.debug(s"checking directories '${ConfigMover.CONFIG_PATH_SOURCES}' in bundle ${bundle.getSymbolicName()} for configurations")
    ConfigMover.CONFIG_PATH_SOURCES.split(",").map(_.trim).toList
  }

  private def handleConfigFiles(bundle: Bundle, entryPaths: Iterator[String]): Boolean = {
    if (entryPaths == null) {
      log.warn("entryPaths is null, stopping copy process...")
      return false
    }
    var configCreated = false
    while (entryPaths.hasNext) {
      val sourceFileName = entryPaths.next
      val content = ConfigMover.readResource(bundle, sourceFileName)
      try {
        configCreated = copyFileIfNotExists(sourceFileName, content) || configCreated
      } catch {
        case e: Throwable => log.error(e.getMessage(), e)
      }
    }
    return configCreated
  }

  private def copyFileIfNotExists(sourceFileName: String, content: String): Boolean = {
    val targetFilePath = Paths.get(("./" + sourceFileName).replace("/standalone", ""))
    log.info(s"checking if file '${targetFilePath}' exists...")
    if (Files.exists(targetFilePath)) {
      log.debug(s"not copying '$sourceFileName', as it already exists in ${targetFilePath.toString()}")
      if (targetFilePath.toString().contains("logback.xml")) {
        logbackConfigurationExists = true
      }
      return false
    }
    log.info("about to copy configuration from product bundle to '{}'", targetFilePath.toString())
    Files.write(targetFilePath, content.getBytes())
    true

  }

}