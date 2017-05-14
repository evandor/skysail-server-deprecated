package io.skysail.doc.swagger

import scala.annotation.meta.field
import scala.beans.BeanProperty

case class SwaggerExternalDoc(
    @BeanProperty val description: String,
    @BeanProperty val url:String
)