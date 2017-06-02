package io.skysail.converter

import io.skysail.converter.st.wrapper._
import io.skysail.core.app._
import io.skysail.restlet.SkysailServerResource
import io.skysail.core.app.SkysailApplicationService
import io.skysail.restlet.queries.QueryFilterParser
import io.skysail.restlet.responses.ScalaSkysailResponse
import io.skysail.core.app.SkysailApplication
import io.skysail.restlet.ResourceContextId
import io.skysail.restlet.utils._
import org.restlet.representation.{ StringRepresentation, Variant }
import org.restlet.resource.Resource
import org.restlet.data.MediaType
import org.slf4j.LoggerFactory
import org.stringtemplate.v4.{ ST, STGroup }
import org.osgi.framework._
import java.util.Optional
import java.util.Arrays
import scala.collection.JavaConverters._
import io.skysail.domain.ddd.ScalaEntity

object StringTemplateRenderer {
  val SKYSAIL_SERVER_CONVERTER = "skysail.converter";
  val TEMPLATES_DIR = "/templates";
  val INDEX_FOR_MOBILES = "indexMobile";
}

class StringTemplateRenderer(htmlConverter: HtmlConverter, resource: SkysailServerResource[_]) {

  val log = LoggerFactory.getLogger(classOf[StringTemplateRenderer])

  var skysailApplicationService: SkysailApplicationService = null
  var filterParser: QueryFilterParser = null

  def setFilterParser(f: QueryFilterParser) = filterParser = f

  def createRepresenation(entity: ScalaSkysailResponse[_], target: Variant): StringRepresentation = {
    val styling = Styling.determineFrom(resource);
    val theming = Theming.determineFrom(resource, target)
    STGroupBundleDir.clearUsedTemplates();
    val stGroup = createStringTemplateGroup(resource, styling, theming);
    val index = getStringTemplateIndex(resource, styling, stGroup);
    val resourceModel = createResourceModel(entity, target, theming, resource);
    addSubstitutions(resourceModel, index);
    //  checkForInspection(resource, index);
    return createRepresentation(index, stGroup);
  }

  def createStringTemplateGroup(resource: Resource, styling: Styling, theme: Theming) = {
    import StringTemplateRenderer._
    val stGroup = new STGroupBundleDir(determineBundleToUse(), resource, TEMPLATES_DIR, htmlConverter.getTemplateProvider());
//    stGroup.registerModelAdaptor(classOf[ScalaEntity[_]], new ObjectModelAdaptor() {
//      override def getProperty(interpreter: Interpreter, self: ST, o: AnyRef, property: AnyRef, propertyName: String): AnyRef = {
//        //if (propertyName.equals("name")) return ((User)o).name.substring(0, 1).toUpperCase() + ((User)o).name.substring(1);
//        //if (propertyName.equals("description")) return "User object with id:" + ((User)o).id;
//        println(propertyName)
//        return super.getProperty(interpreter, self, o, property, propertyName);
//      }
//    })
    importFrom(resource, theme, stGroup, SKYSAIL_SERVER_CONVERTER);
    importFrom(resource, theme, stGroup, System.getProperty(io.skysail.core.Constants.PRODUCT_BUNDLE_IDENTIFIER));
    stGroup;
  }

  def resourcePathExists(resourcePath: String, theBundle: Bundle) = theBundle.getResource(resourcePath) != null

  def setSkysailApplicationService(service: SkysailApplicationService) = this.skysailApplicationService = service

  private def determineBundleToUse(): Bundle = {
    if (bundleProvidesTemplates(appBundle)) {
      return appBundle;
    }
    return findBundle(appBundle.getBundleContext(), StringTemplateRenderer.SKYSAIL_SERVER_CONVERTER);
  }

  private def findBundle(bundleContext: BundleContext, bundleName: String) = {
    bundleContext.getBundles().filter { b => b.getSymbolicName().equals(bundleName) }.head
  }

  private def appBundle = resource.getApplication().asInstanceOf[SkysailApplication].getBundle
  private def bundleProvidesTemplates(appBundle: Bundle) = appBundle.getResource(StringTemplateRenderer.TEMPLATES_DIR) != null

  private def getStringTemplateIndex(resource: Resource, styling: Styling, stGroup: STGroup): ST = {
    if (resource.isInstanceOf[SkysailServerResource[_]]
      && resource.getContext() != null
      && resource.asInstanceOf[SkysailServerResource[_]].getFromContext(ResourceContextId.RENDERER_HINT) != null) {
      val root = resource.asInstanceOf[SkysailServerResource[_]].getFromContext(ResourceContextId.RENDERER_HINT);
      resource.getContext().getAttributes().remove(ResourceContextId.RENDERER_HINT.name());
      return stGroup.getInstanceOf(root + "_index");
    }

    if (RequestUtils.isMobile(resource.getRequest())) {
      return stGroup.getInstanceOf("indexMobile");
    }
    if (styling.getName().length() > 0) {
      val instanceOf = stGroup.getInstanceOf(styling.getName() + "_index");
      if (instanceOf == null) {
        log.warn("could not find templates for style '{}', falling back to default", styling.getName())
        return stGroup.getInstanceOf("index");
      }
      return instanceOf;
    }

    val identifier = getIndexPageNameFromCookie(resource).orElse(Some("index"))
    return stGroup.getInstanceOf(identifier.get);

  }

  private def getIndexPageNameFromCookie(resource: Resource): Option[String] = {
    return ScalaCookiesUtils.getMainPageFromCookie(resource.getRequest());
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

  private def createResourceModel(entity: Any, target: Variant, theming: Theming, resource: SkysailServerResource[_]): ResourceRenderingModel = {

    val resourceModel = new ResourceRenderingModel(resource, entity.asInstanceOf[ScalaSkysailResponse[_]], htmlConverter.getUserManagementProvider(), target, theming);
    //        resourceModel.setMenuItemProviders(menuProviders);
    resourceModel.setFilterParser(filterParser);
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

  private def addSubstitutions(resourceModel: ResourceRenderingModel, decl: ST): Unit = {

    val resource = resourceModel.getResource();

    val installationFromCookie = ScalaCookiesUtils.getInstallationFromCookie(resource.getRequest()).getOrElse(null);

    decl.add("user", new StUserWrapper(htmlConverter.getUserManagementProvider(), resourceModel.getResource(),
      installationFromCookie));
    decl.add("converter", this);
    //
    val fs = resourceModel.fields.values
    decl.add("messages", resource.getMessages(fs))
    decl.add("model", resourceModel);
    decl.add("request", new StRequestWrapper(
      resource.getRequest(),
      resourceModel.getFormfields().asScala.map(f => f.getId).toList));
  }

}