package io.skysail.converter

import org.osgi.service.component.annotations.Component
import io.skysail.core.osgi.services.OsgiConverterHelper
import org.restlet.ext.jackson.JacksonConverter
import org.restlet.representation.Variant
import org.restlet.resource.Resource
import org.restlet.representation.Representation
import io.skysail.core.restlet.SkysailServerResource
import org.restlet.ext.jackson.JacksonRepresentation
import org.restlet.data.MediaType
import io.skysail.core.restlet.responses.ScalaSkysailResponse
import scala.collection.JavaConverters._

@Component(immediate = true)
class SkysailJacksonConverter extends JacksonConverter with OsgiConverterHelper {

  override def toRepresentation(source: AnyRef, target: Variant, resource: Resource): Representation = {
    if (source.isInstanceOf[ScalaSkysailResponse[_]]) {
      val entity = source.asInstanceOf[ScalaSkysailResponse[_]].entity
      if (resource.getQuery().getNames().contains("_rendered")) {
        val skysailServerResource = resource.asInstanceOf[SkysailServerResource[_]]

        val resourceModel = new ResourceRenderingModel(skysailServerResource, source.asInstanceOf[ScalaSkysailResponse[_]], null, target, new Theming());

        val messages = Map() //skysailServerResource.getMessages(resourceModel.getFields());
        val descrition = "desc" //messages.get("content.header");

        return super.toRepresentation(resourceModel.getRawData(), target, resource);
      }
      //            if (target.getMediaType().equals(MediaType.TEXT_CSV)) {
      //                val jacksonRepresentation = new JacksonRepresentation[java.util.List[_]](MediaType.TEXT_CSV, entity.asInstanceOf[List[_]]) {
      //                    override protected def createObjectWriter() {
      //                        val csvMapper = getObjectMapper().asInstanceOf[CsvMapper]
      //                       val cls = ((List<?>)entity).get(0).getClass();
      //                        val csvSchema = csvMapper.schemaFor(cls);
      //                        val result = csvMapper.writer(csvSchema);
      //                        return result;
      //                    }
      //                };
      //                return jacksonRepresentation;
      //            }
      if (entity.isInstanceOf[List[_]]) {
        return super.toRepresentation(entity.asInstanceOf[List[_]].asJava, target, resource);
      }
      return super.toRepresentation(entity, target, resource);
    }
    return super.toRepresentation(source, target, resource);
  }
}
