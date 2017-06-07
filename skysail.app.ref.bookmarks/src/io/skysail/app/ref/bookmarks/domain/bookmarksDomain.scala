package io.skysail.app.ref.bookmarks.domain

import scala.annotation.meta.field
import io.skysail.core.html.Field
import scala.beans.BeanProperty
import io.skysail.core.domain.ScalaEntity

/**
 * the most simple Bookmark Entity consisting of title and url, with
 * no validations enforced.
 */
case class Bookmark(

  var id: Option[String] = None,
  @BeanProperty @(Field @field) var title: String,
  @BeanProperty @(Field @field) var url: String

) extends ScalaEntity[String]
