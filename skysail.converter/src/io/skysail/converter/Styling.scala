package io.skysail.converter

import org.restlet.resource.Resource
import io.skysail.core.restlet.SkysailServerResource
import io.skysail.restlet.utils.ScalaCookiesUtils
import io.skysail.core.Constants

object Styling {
  val DEFAULT_STYLING = "";

  /**
   * @return e.g. bootstrap, semanticui, jquerymobile
   */
  def determineFrom(resource: SkysailServerResource[_]): Styling = {
    if (resource.getQuery() == null || resource.getQuery().getFirstValue("_styling") == null) {
      return stylingFromCookieOrDefault(resource)
    }
    val stylingFromRequest: String = resource.getQuery().getFirstValue("_styling");
    val styling = Styling(stylingFromRequest, stylingFromRequest, true)
    val stylingCookie = ScalaCookiesUtils.createCookie(Constants.COOKIE_NAME_STYLING, "/", -1);
    stylingCookie.setValue(stylingFromRequest);
    resource.getResponse().getCookieSettings().add(stylingCookie);
    return styling;
  }

  private def stylingFromCookieOrDefault(resource: Resource): Styling = {
    val styling = ScalaCookiesUtils.getStylingFromCookie(resource.getRequest()).orElse(Some(DEFAULT_STYLING));
    Styling(styling.get, "", false);
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