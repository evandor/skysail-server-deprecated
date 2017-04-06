package io.skysail.repo.orientdb.helper

object DbClassName {
  def of(cls: Class[_]) = cls.getName().replace(".", "_")
}