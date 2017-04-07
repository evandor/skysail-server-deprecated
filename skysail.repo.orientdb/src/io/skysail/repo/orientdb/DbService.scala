package io.skysail.repo.orientdb

trait ScalaDbService {
  def createWithSuperClass(superClass: String, vertices: String*)
  def register(classes: Class[_]*)
}