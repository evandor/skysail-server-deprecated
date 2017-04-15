package io.skysail.converter

import java.util.Collections
import java.util.Arrays
import io.skysail.api.responses.SkysailResponse
import io.skysail.restlet.ScalaSkysailServerResource
import io.skysail.api.um.UserManagementProvider
import io.skysail.restlet.responses.ScalaSkysailResponse
import io.skysail.restlet.queries.QueryFilterParser
import io.skysail.restlet.services.StringTemplateProvider
import io.skysail.restlet.app._
import org.restlet.engine.resource.VariantInfo
import org.restlet.representation.Representation
import org.restlet.resource.Resource
import org.osgi.service.component.annotations._
import org.restlet.engine.converter.ConverterHelper
import org.restlet.representation.Variant
import org.restlet.data.MediaType
import org.slf4j.LoggerFactory
import io.skysail.restlet.app.SkysailApplicationService
import io.skysail.core.osgi.services.OsgiConverterHelper
import io.skysail.core.osgi.services.OsgiConverterHelper
import org.restlet.engine.Engine

object ScalaHtmlConverter {
  val DEFAULT_MATCH_VALUE = 0.5f;
  def mediaTypesMatch(): Map[MediaType, Float] = {
    val result = Map[MediaType, Float]()
    // result += ((MediaType.TEXT_HTML, 0.95F)
    result
  }
}

/**
 * A restlet converter (extending ConverterHelper) as an OSGi service, implementing the marker
 * interface OsgiConverterHelper.
 *
 * The marker interface is used by ScalaHttpServer to add this converter to the
 *
 */
@Component(immediate = true, service = Array(classOf[OsgiConverterHelper]))
class ScalaHtmlConverter extends ConverterHelper with OsgiConverterHelper {

  val log = LoggerFactory.getLogger(getClass())

  @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
  @volatile var templateProvider: java.util.List[StringTemplateProvider] = new java.util.ArrayList[StringTemplateProvider]();
  def getTemplateProvider() = templateProvider

  /** --- mandatory reference ------------------------- */
  @Reference(cardinality = ReferenceCardinality.MANDATORY)
  var userManagementProvider: UserManagementProvider = null
  def getUserManagementProvider() = userManagementProvider

  @Reference(cardinality = ReferenceCardinality.OPTIONAL)
  var skysailApplicationService: SkysailApplicationService = null
  def getSkysailApplicationService() = skysailApplicationService

  @Reference(cardinality = ReferenceCardinality.OPTIONAL)
  var filterParser: QueryFilterParser = null

  @Activate def activate():Unit = {
    log.info(s"activating ${getClass().getName}")
    Engine.getInstance().getRegisteredConverters().add(this)
  }

  @Deactivate def deactivate():Unit = {
    log.info(s"deactivating ${getClass().getName}")
    Engine.getInstance().getRegisteredConverters().remove(this)
  }

  def getObjectClasses(variant: Variant) = Collections.emptyList()

  def score[T](rep: Representation, cls: Class[T], res: Resource) = -1.0F

  def getVariants(x$1: Class[_]): java.util.List[VariantInfo] = {
    Arrays.asList( //      new VariantInfo(SecurityConfigBuilder.SKYSAIL_TREE_FORM),
    //      new VariantInfo(SecurityConfigBuilder.SKYSAIL_MAILTO_MEDIATYPE),
    //      new VariantInfo(SecurityConfigBuilder.SKYSAIL_TIMELINE_MEDIATYPE),
    //      new VariantInfo(SecurityConfigBuilder.SKYSAIL_STANDLONE_APP_MEDIATYPE)
    )
  }

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

  def toObject[T](rep: Representation, cls: Class[T], res: Resource): T = {
    throw new RuntimeException("toObject method is not implemented yet");
  }

  def toRepresentation(skysailResponse: Any, target: Variant, resource: Resource): Representation = {
    require(skysailResponse.isInstanceOf[ScalaSkysailResponse[_]])
    val stringTemplateRenderer = new StringTemplateRenderer(this, resource.asInstanceOf[ScalaSkysailServerResource]);
    //    stringTemplateRenderer.setMenuProviders(menuProviders);
    stringTemplateRenderer.setFilterParser(filterParser);
    //    stringTemplateRenderer.setInstallationProvider(installationProvider);
    stringTemplateRenderer.setSkysailApplicationService(skysailApplicationService);
    val ssr = skysailResponse.asInstanceOf[ScalaSkysailResponse[_]]
    return stringTemplateRenderer.createRepresenation(ssr, target);
  }
}