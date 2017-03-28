package io.skysail.converter

import org.osgi.service.component.annotations._
import org.restlet.engine.converter.ConverterHelper
import io.skysail.server.services.OsgiConverterHelper
import org.restlet.representation.Variant
import java.util.Collections
import org.restlet.engine.resource.VariantInfo
import java.util.Arrays
import io.skysail.core.app.SkysailApplication
import org.restlet.representation.Representation
import org.restlet.resource.Resource
import org.restlet.data.MediaType
import io.skysail.api.responses.SkysailResponse
import io.skysail.core.resources.SkysailServerResource
import io.skysail.restlet.ScalaSkysailServerResource
import io.skysail.server.services.StringTemplateProvider
import io.skysail.api.um.UserManagementProvider

object ScalaHtmlConverter {
  val DEFAULT_MATCH_VALUE = 0.5f;
  def mediaTypesMatch(): Map[MediaType, Float] = {
    val result = Map[MediaType, Float]()
    // result += ((MediaType.TEXT_HTML, 0.95F)
    result
  }

}

@Component(immediate = true) //, property = { "event.topics=" + EventHelper.GUI +"/*"})
class ScalaHtmlConverter extends ConverterHelper with OsgiConverterHelper {
  
  @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
  var templateProvider = new java.util.ArrayList[StringTemplateProvider]();
  def getTemplateProvider() = templateProvider
  
  @Reference(cardinality = ReferenceCardinality.MANDATORY)
  var userManagementProvider: UserManagementProvider = null
  def getUserManagementProvider() = userManagementProvider


  def getObjectClasses(x$1: Variant): java.util.List[Class[_]] = Collections.emptyList()

  def getVariants(x$1: Class[_]): java.util.List[VariantInfo] = {
    Arrays.asList(
      new VariantInfo(SkysailApplication.SKYSAIL_TREE_FORM),
      new VariantInfo(SkysailApplication.SKYSAIL_MAILTO_MEDIATYPE),
      new VariantInfo(SkysailApplication.SKYSAIL_TIMELINE_MEDIATYPE),
      new VariantInfo(SkysailApplication.SKYSAIL_STANDLONE_APP_MEDIATYPE))
  }

  def score[T](x$1: Representation, x$2: Class[T], x$3: Resource) = -1.0F

  def score(source: Any, target: Variant, resource: Resource): Float = {
    if (target == null) {
      return 0.0f;
    }
    /*for (mediaType <- ScalaHtmlConverter.mediaTypesMatch().keySet()) {
      if (target.getMediaType().equals(mediaType)) {
        return ScalaHtmlConverter.mediaTypesMatch.get(mediaType);
      }
    }*/
    return ScalaHtmlConverter.DEFAULT_MATCH_VALUE;

  }

  def toObject[T](x$1: Representation, x$2: Class[T], x$3: Resource): T = {
    throw new RuntimeException("toObject method is not implemented yet");
  }

  def toRepresentation(skysailResponse: Any, target: Variant, resource: Resource): Representation = {
    val stringTemplateRenderer = new StringTemplateRenderer(this, resource);
    //    stringTemplateRenderer.setMenuProviders(menuProviders);
    //    stringTemplateRenderer.setFilterParser(filterParser);
    //    stringTemplateRenderer.setInstallationProvider(installationProvider);
    //    stringTemplateRenderer.setSkysailApplicationService(skysailApplicationService);
    return stringTemplateRenderer.createRepresenation(skysailResponse.asInstanceOf[SkysailResponse[_]], target, resource.asInstanceOf[ScalaSkysailServerResource]);
  }
}