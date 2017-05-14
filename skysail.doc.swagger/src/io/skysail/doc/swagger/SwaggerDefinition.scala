package io.skysail.doc.swagger

import org.slf4j.LoggerFactory
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include
import scala.annotation.meta.field
import scala.beans.BeanProperty

@JsonInclude(Include.NON_NULL)
class SwaggerDefinition(entity: Class[_]) {

  private val log = LoggerFactory.getLogger(this.getClass())

  @BeanProperty val properties = new java.util.HashMap[String, SwaggerProperty]()

  try {
    //val newInstance = entity.newInstance()
    val fields = entity.getDeclaredFields()
    fields
      .toList
      .filter(f => f.getDeclaredAnnotation(classOf[io.skysail.core.html.Field]) != null)
      .foreach(f => properties.put(f.getName,new SwaggerProperty(f)))

    //val declaredAnnotation = entity.getClass().getDeclaredAnnotation(classOf[io.skysail.core.html.Field])

  } catch { case e: Any => log.error(e.getMessage(), e) }

}
