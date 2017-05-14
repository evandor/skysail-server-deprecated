package io.skysail.doc.swagger

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude._
import scala.annotation.meta.field
import scala.beans.BeanProperty

@JsonInclude(content = Include.NON_NULL)
case class SwaggerParameter(@BeanProperty val name: String) {
  @BeanProperty val in = "path"
  @BeanProperty val description = "default swagger parameter description";
  @BeanProperty val required = true;
  @BeanProperty val `type` = "string";
}