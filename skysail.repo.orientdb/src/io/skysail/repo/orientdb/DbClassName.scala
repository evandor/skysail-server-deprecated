package io.skysail.repo.orientdb

object DbClassName {
  def of(cls: Class[_]) = cls.getName().replace(".", "_")
}