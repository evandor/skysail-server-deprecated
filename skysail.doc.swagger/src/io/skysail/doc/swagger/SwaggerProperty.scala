package io.skysail.doc.swagger

import java.lang.reflect.Field
import scala.annotation.meta.field
import scala.beans.BeanProperty
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include

@JsonInclude(Include.NON_NULL)
case class SwaggerProperty(f: Field) {
  private val domainFieldAnnotation = f.getDeclaredAnnotation(classOf[io.skysail.core.html.Field])
  @BeanProperty val `type` = "string"//f.getType().getSimpleName().toLowerCase()
  @BeanProperty val description = domainFieldAnnotation.description()
}
