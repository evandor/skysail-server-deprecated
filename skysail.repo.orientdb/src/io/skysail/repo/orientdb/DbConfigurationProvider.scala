package io.skysail.repo.orientdb

trait DbConfigurationProvider {
  def getConfig(): ScalaDbConfig
}