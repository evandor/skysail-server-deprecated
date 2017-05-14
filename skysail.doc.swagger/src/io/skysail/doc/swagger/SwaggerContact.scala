package io.skysail.doc.swagger
import scala.annotation.meta.field
import scala.beans.BeanProperty

case class SwaggerContact(
    @BeanProperty val name:String, 
    @BeanProperty val url: String, 
    @BeanProperty val email:String
)