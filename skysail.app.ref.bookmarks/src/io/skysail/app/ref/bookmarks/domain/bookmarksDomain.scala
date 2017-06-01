package io.skysail.app.ref.bookmarks.domain

import scala.annotation.meta.field
import io.skysail.domain.ddd.ScalaEntity
import io.skysail.core.html.Field
import scala.beans.BeanProperty

case class Bookmark(

  var id: Option[String] = None,
  @BeanProperty @(Field @field) var title: String,
  @BeanProperty @(Field @field) var url: String) extends ScalaEntity[String]
