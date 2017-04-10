package io.skysail.repo.orientdb

import org.slf4j.LoggerFactory
import org.restlet.engine.util.StringUtils

object AbstractOrientDbService {
  val DB_URL = "db.url"
  val DEFAULT_DB_URL = "memory:skysail"
  val DEFAULT_DB_USERNAME = "admin"
  val DEFAULT_DB_PASSWORD = "admin"
}

abstract class AbstractOrientDbService {

  val log = LoggerFactory.getLogger(classOf[AbstractOrientDbService])

  var provider: DbConfigurationProvider = null
  var started = false

  protected def registerShutdownHook()
  protected def startDb()
  protected def stopDb()

  def updated(provider: DbConfigurationProvider): Unit = {
    this.provider = provider
    if (provider == null || provider.getConfig() == null) {
      log.warn("DbConfigurationProvider was null or has null-config, stopping db")
      stopDb()
      return
    }
    try {
      startDb()
      registerShutdownHook()
    } catch {
      case e: Throwable => log.error(e.getMessage,e)
      //              case e: ODatabaseException => 
      //                if (!e.getMessage().startsWith("Database instance has been released to the pool.")) {
      //                    throw e
      //                }
      //                try {
      //                    Thread.sleep(1000)
      //                } catch (InterruptedException e1) {
      //                    log.error(e1.getMessage(), e1)
      //                }
    }
  }

  protected def getDbUrl(): String = {
    if (provider == null || provider.getConfig() == null) {
      return AbstractOrientDbService.DEFAULT_DB_URL
    }
    val url = provider.getConfig().url
    if (url != null) {
      return url
    }
    return AbstractOrientDbService.DEFAULT_DB_URL
  }

  protected def getDbUsername(): String = {
    if (provider == null || provider.getConfig() == null) {
      log.warn("falling back to default username as provider is null")
      return AbstractOrientDbService.DEFAULT_DB_USERNAME
    }
    val username = provider.getConfig().username
    if (StringUtils.isNullOrEmpty(username)) {
      log.warn("falling back to default username as username is null or empty")
      return AbstractOrientDbService.DEFAULT_DB_USERNAME
    }
    username
  }

  protected def getDbPassword(): String = {
    if (provider == null || provider.getConfig() == null) {
      log.warn("falling back to default password as provider is null")
      return AbstractOrientDbService.DEFAULT_DB_PASSWORD
    }
    val password = provider.getConfig().password
    if (StringUtils.isNullOrEmpty(password)) {
      log.warn("falling back to default password as password is null or empty")
      return AbstractOrientDbService.DEFAULT_DB_PASSWORD
    }
    password
  }

}