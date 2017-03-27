package io.skysail.converter

import org.restlet.representation.Variant
import io.skysail.api.responses.SkysailResponse
import io.skysail.core.resources.SkysailServerResource
import org.restlet.representation.StringRepresentation
import org.restlet.resource.Resource
import io.skysail.restlet.ScalaSkysailServerResource
import io.skysail.server.rendering.Theme
import org.osgi.framework.Bundle
import org.osgi.framework.BundleContext
import java.util.Optional
import java.util.Arrays
import io.skysail.core.app.SkysailApplication

object StringTemplateRenderer {
  val SKYSAIL_SERVER_CONVERTER = "skysail.converter";
  val TEMPLATES_DIR = "/templates";
  val INDEX_FOR_MOBILES = "indexMobile";
}

class StringTemplateRenderer(htmlConverter: ScalaHtmlConverter, resource: Resource) {
  def createRepresenation(entity: SkysailResponse[_], target: Variant, resource: ScalaSkysailServerResource): StringRepresentation = {

    val styling = Styling.determineFrom(resource); // e.g. bootstrap, semanticui, jquerymobile
    val theme = Theme(); //Theme.determineFrom(resource, target);
    //
    //STGroupBundleDir.clearUsedTemplates();
    val stGroup = createStringTemplateGroup(resource, styling, theme);
    //        ST index = getStringTemplateIndex(resource, styling, stGroup);
    //
    //        ResourceModel<SkysailServerResource<?>, ?> resourceModel = createResourceModel(entity, target, resource);
    //
    //        addSubstitutions(resourceModel, index);
    //
    //        checkForInspection(resource, index);

    return new StringRepresentation("hi"); //createRepresentation(index, stGroup);
  }

  def createStringTemplateGroup(resource: Resource, styling: Styling, theme: Theme) = {
    import StringTemplateRenderer._
    val stGroup = new STGroupBundleDir(determineBundleToUse(), resource, TEMPLATES_DIR, htmlConverter.getTemplateProvider());
    //        importFrom(resource, theme, stGroup, SKYSAIL_SERVER_CONVERTER);
    //        importFrom(resource, theme, stGroup, System.getProperty(Constants.PRODUCT_BUNDLE_IDENTIFIER));
    //        return stGroup;

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

  private def appBundle = resource.getApplication().asInstanceOf[SkysailApplication].getBundle
  private def bundleProvidesTemplates(appBundle: Bundle) = appBundle.getResource(StringTemplateRenderer.TEMPLATES_DIR) != null

}