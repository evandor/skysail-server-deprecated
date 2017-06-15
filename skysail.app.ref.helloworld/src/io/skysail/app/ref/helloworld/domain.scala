package io.skysail.app.ref.helloworld

import io.skysail.core.html.Field
import scala.beans.BeanProperty
import io.skysail.core.domain.ScalaEntity
import scala.annotation.meta.field

case class Hello(

  var id: Option[String] = None,
  @BeanProperty @(Field @field) var name: String

) extends ScalaEntity[String]
