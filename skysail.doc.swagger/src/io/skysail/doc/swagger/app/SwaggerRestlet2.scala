package io.skysail.doc.swagger.app

import org.restlet.Restlet
import io.skysail.core.app.SkysailApplication
import com.fasterxml.jackson.databind.ObjectMapper
import org.restlet.Response
import org.restlet.Request
import io.skysail.doc.swagger.SwaggerSpec2
import com.fasterxml.jackson.core.JsonProcessingException
import org.restlet.representation.StringRepresentation

object SwaggerRestlet2 { val mapper = new ObjectMapper() }

class SwaggerRestlet2(application: SkysailApplication) extends Restlet {

  override def handle(request: Request, response: Response) = {
    val swaggerSpec = new SwaggerSpec2(application, request);
    try {
      val swaggerApi = SwaggerRestlet2.mapper.writeValueAsString(swaggerSpec);
      response.setEntity(new StringRepresentation(swaggerApi)); //, MediaType.APPLICATION_YAML));
    } catch {
      case e: JsonProcessingException => //log.error(e.getMessage(), e);
    }
  }

}
