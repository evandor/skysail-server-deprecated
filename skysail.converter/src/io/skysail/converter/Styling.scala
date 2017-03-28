package io.skysail.converter

import io.skysail.core.utils.CookiesUtils
import io.skysail.server.Constants
import org.restlet.resource.Resource
import io.skysail.restlet.ScalaSkysailServerResource

object Styling {
  val DEFAULT_STYLING = "";
  def determineFrom(resource: ScalaSkysailServerResource): Styling = {
    if (resource.getQuery() == null || resource.getQuery().getFirstValue("_styling") == null) {
      return stylingFromCookieOrDefault(resource)
    }
    val stylingFromRequest: String = resource.getQuery().getFirstValue("_styling");
    val styling = Styling(stylingFromRequest, stylingFromRequest, true)
    val stylingCookie = CookiesUtils.createCookie(Constants.COOKIE_NAME_STYLING, "/", -1);
    stylingCookie.setValue(stylingFromRequest);
    resource.getResponse().getCookieSettings().add(stylingCookie);
    return styling;
  }

  private def stylingFromCookieOrDefault(resource: Resource): Styling = {
    val styling = CookiesUtils.getStylingFromCookie(resource.getRequest()).orElse(DEFAULT_STYLING);
    Styling(styling, "", false);
  }
}

case class Styling(styling: String, shortName: String, selected: Boolean) extends Ordered[Styling] {
  def compare(that: Styling): Int = styling.compareTo(that.styling)
  def getLabel() = firstUppercaseOf(styling)
  def getName() = styling
  private def firstUppercaseOf(styling: String): String = {
    if (styling.length() > 0) {
      return styling.substring(0, 1).toUpperCase() + styling.substring(1);
    }
    return "";
  }
}