package io.skysail.doc.swagger.app

import org.restlet.Restlet
import io.skysail.core.app.SkysailApplication
import com.fasterxml.jackson.databind.ObjectMapper
import org.restlet.Response
import org.restlet.Request
import com.fasterxml.jackson.core.JsonProcessingException
import org.restlet.representation.StringRepresentation
import org.slf4j.LoggerFactory
import io.skysail.doc.swagger.SwaggerSpec

object SwaggerRestlet { val mapper = new ObjectMapper() }

class SwaggerRestlet(application: SkysailApplication) extends Restlet {
  
  private val log = LoggerFactory.getLogger(this.getClass())

  override def handle(request: Request, response: Response) = {
    val swaggerSpec = new SwaggerSpec(application, request);
    try {
      val swaggerApi = SwaggerRestlet.mapper.writeValueAsString(swaggerSpec);
      response.setEntity(new StringRepresentation(swaggerApi)); //, MediaType.APPLICATION_YAML));
    } catch {
      case e: JsonProcessingException => log.error(e.getMessage(), e);
    }
  }

}
