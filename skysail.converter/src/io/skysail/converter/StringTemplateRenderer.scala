package io.skysail.converter

import io.skysail.api.responses.SkysailResponse
import io.skysail.core.app._
import io.skysail.core.resources.SkysailServerResource
import io.skysail.core.utils.CookiesUtils
import io.skysail.restlet.ScalaSkysailServerResource
import io.skysail.server.rendering.Theme
import io.skysail.server.ResourceContextId
import io.skysail.server.utils.RequestUtils
import io.skysail.server.Constants
import io.skysail.restlet.responses.ScalaSkysailResponse
import io.skysail.restlet.app.ScalaSkysailApplication
import org.restlet.representation.StringRepresentation
import org.restlet.resource.Resource
import org.restlet.data.MediaType
import org.restlet.representation.Variant
import org.stringtemplate.v4.STGroup
import org.osgi.framework._
import org.stringtemplate.v4.ST
import java.util.Optional
import java.util.Arrays
import io.skysail.restlet.app.ScalaSkysailApplicationService

object StringTemplateRenderer {
  val SKYSAIL_SERVER_CONVERTER = "skysail.converter";
  val TEMPLATES_DIR = "/templates";
  val INDEX_FOR_MOBILES = "indexMobile";
}

class StringTemplateRenderer(htmlConverter: ScalaHtmlConverter, resource: Resource) {
  
  var skysailApplicationService: ScalaSkysailApplicationService = null
  
  def createRepresenation(entity: ScalaSkysailResponse[_], target: Variant, resource: ScalaSkysailServerResource): StringRepresentation = {
    val styling = Styling.determineFrom(resource); // e.g. bootstrap, semanticui, jquerymobile
    val theming = Theming.determineFrom(resource, target)
    STGroupBundleDir.clearUsedTemplates();
    val stGroup = createStringTemplateGroup(resource, styling, theming);
    val index = getStringTemplateIndex(resource, styling, stGroup);
    val resourceModel = createResourceModel(entity, target, theming, resource);
    //  addSubstitutions(resourceModel, index);
    //  checkForInspection(resource, index);
    return createRepresentation(index, stGroup);
  }

  def createStringTemplateGroup(resource: Resource, styling: Styling, theme: Theming) = {
    import StringTemplateRenderer._
    val stGroup = new STGroupBundleDir(determineBundleToUse(), resource, TEMPLATES_DIR, htmlConverter.getTemplateProvider());
    importFrom(resource, theme, stGroup, SKYSAIL_SERVER_CONVERTER);
    importFrom(resource, theme, stGroup, System.getProperty(Constants.PRODUCT_BUNDLE_IDENTIFIER));
    stGroup;
  }

  private def determineBundleToUse(): Bundle = {
    if (bundleProvidesTemplates(appBundle)) {
      return appBundle;
    }
    return findBundle(appBundle.getBundleContext(), StringTemplateRenderer.SKYSAIL_SERVER_CONVERTER);
  }

  private def findBundle(bundleContext: BundleContext, bundleName: String) = {
    bundleContext.getBundles().filter { b => b.getSymbolicName().equals(bundleName) }.head
  }

  private def appBundle = resource.getApplication().asInstanceOf[ScalaSkysailApplication].getBundle
  private def bundleProvidesTemplates(appBundle: Bundle) = appBundle.getResource(StringTemplateRenderer.TEMPLATES_DIR) != null

  private def getStringTemplateIndex(resource: Resource, styling: Styling, stGroup: STGroup): ST = {
    if (resource.isInstanceOf[ScalaSkysailServerResource]
      && resource.getContext() != null
      && resource.asInstanceOf[ScalaSkysailServerResource].getFromContext(ResourceContextId.RENDERER_HINT) != null) {
      val root = resource.asInstanceOf[ScalaSkysailServerResource].getFromContext(ResourceContextId.RENDERER_HINT);
      resource.getContext().getAttributes().remove(ResourceContextId.RENDERER_HINT.name());
      return stGroup.getInstanceOf(root + "_index");
    }

    if (RequestUtils.isMobile(resource.getRequest())) {
      return stGroup.getInstanceOf("indexMobile");
    }
    if (styling.getName().length() > 0) {
      val instanceOf = stGroup.getInstanceOf(styling.getName() + "_index");
      if (instanceOf == null) {
        // fallback
        return stGroup.getInstanceOf("index");
      }
      return instanceOf;
    }

    val identifier = getIndexPageNameFromCookie(resource).orElse("index")
    return stGroup.getInstanceOf(identifier);

  }

  private def getIndexPageNameFromCookie(resource: Resource): Optional[String] = {
    return CookiesUtils.getMainPageFromCookie(resource.getRequest());
  }

  private def importFrom(resource: Resource, theme: Theming, stGroup: STGroupBundleDir, symbolicName: String): Unit = {
    val theBundle = findBundle(appBundle.getBundleContext(), symbolicName);
    importTemplates(theBundle, resource, StringTemplateRenderer.TEMPLATES_DIR, stGroup, theme);
  }

  private def importTemplates(theBundle: Bundle, resource: Resource, resourcePath: String, stGroup: STGroupBundleDir, theme: Theming): Unit = {
    val mediaTypedResourcePath = (resourcePath + "/" + theme.variant.identifier).replace("/*", "")
    importTemplates(resource, mediaTypedResourcePath, stGroup, theBundle)
    importTemplates(resource, mediaTypedResourcePath + "/head", stGroup, theBundle)
    importTemplates(resource, mediaTypedResourcePath + "/navigation", stGroup, theBundle)
    importTemplates(resource, resourcePath + "/common", stGroup, theBundle)
    importTemplates(resource, resourcePath + "/common/head", stGroup, theBundle)
    importTemplates(resource, resourcePath + "/common/navigation", stGroup, theBundle)
  }

  private def importTemplates(resource: Resource, resourcePath: String, stGroup: STGroupBundleDir, theBundle: Bundle): Unit = {
    if (resourcePathExists(resourcePath, theBundle)) {
      val importedGroupBundleDir = new STGroupBundleDir(theBundle, resource, resourcePath,
        htmlConverter.getTemplateProvider());
      stGroup.importTemplates(importedGroupBundleDir);
    }
  }

  def resourcePathExists(resourcePath: String, theBundle: Bundle) = theBundle.getResource(resourcePath) != null

  private def createResourceModel(entity: Any, target: Variant, theming: Theming, resource: ScalaSkysailServerResource): ResourceModel = {

    val resourceModel = new ResourceModel(resource, entity.asInstanceOf[ScalaSkysailResponse[_]], htmlConverter.getUserManagementProvider(), target, theming);
    //        resourceModel.setMenuItemProviders(menuProviders);
    //        resourceModel.setFilterParser(filterParser);
    //        resourceModel.setInstallationProvider(installationProvider);
    //        resourceModel.setTemplateProvider(htmlConverter.getTemplateProvider());
    resourceModel.setSkysailApplicationService(skysailApplicationService);
    //
    resourceModel.process();
    //
    //        Map<String, Translation> messages = resource.getMessages(resourceModel.getFields());
    //        resourceModel.setMessages(messages);
    //
    return resourceModel;
  }

  private def createRepresentation(index: ST, stGroup: STGroup): StringRepresentation = {
    val stringTemplateRenderedHtml = index.render();
//    if (importedGroupBundleDir != null && stGroup instanceof STGroupBundleDir) {
//      ((STGroupBundleDir) stGroup).addUsedTemplates(STGroupBundleDir.getUsedTemplates());
//    }
    val rep = new StringRepresentation(stringTemplateRenderedHtml);
    rep.setMediaType(MediaType.TEXT_HTML);
    return rep;
  }

  def setSkysailApplicationService(service: ScalaSkysailApplicationService) = {
    this.skysailApplicationService = service
  }

}