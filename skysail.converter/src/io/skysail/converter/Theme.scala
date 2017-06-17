package io.skysail.converter

import io.skysail.core.restlet.SkysailServerResource
import org.restlet.representation.Variant
import java.util.Optional
import io.skysail.restlet.utils.ScalaCookiesUtils
import io.skysail.core.Constants

object Theming {
  
  def determineFrom(resource: SkysailServerResource[_], target: Variant): Theming = {
    if (resource.getQuery() == null || resource.getQuery().getFirstValue("_theme") == null) {
      val themeToUse = ScalaCookiesUtils.getThemeFromCookie(resource.getRequest());
      if (!themeToUse.isDefined) {
          return Theming(BOOTSTRAP)
      }
      if (!target.getMediaType().toString().equals("text/html")) {
          val themeToUse = Optional.of(target.getMediaType().toString());
      }
      return Theming(BOOTSTRAP)//themeFromSplit(themeToUse.get(), themeToUse.get().split("/"));

    }
    val themeFromRequest = resource.getQuery().getFirstValue("_theme")
    val theme = themeFromSplit(themeFromRequest, themeFromRequest.split("/"));
    val themeCookie = ScalaCookiesUtils.createCookie(Constants.COOKIE_NAME_THEME, resource.getRequest().getResourceRef().getPath(), -1);
    themeCookie.setValue(themeFromRequest);
    resource.getResponse().getCookieSettings().add(themeCookie);
    return theme;
  }

  private def createTheme(variant: String, option: String = null) = {
    val theme = new Theming();
    //theme.variant = Variant.valueOf(variant.toUpperCase());
    theme;
  }
  
  private def themeFromSplit(themeAsString: String, split:Array[String]): Theming = {
//      if (split.length == 1) {
//          return createTheme(split[0]);
//      } else if (split.length == 2) {
//          return createTheme(split[0], split[1]);
//      } else {
//          log.warn("could not determine theme from string '{}', using Fallback '{}'", themeAsString, new Theme());
//          return new Theme();
//      }
      Theming(BOOTSTRAP)
    }

}

//SPA, JQUERYMOBILE, UIKIT, PURECSS, W2UI, TIMELINE, HOME, SEMANTICUI
sealed trait ThemingVariant { def identifier: String; def shortName: String }
case object BOOTSTRAP extends ThemingVariant { val identifier = "bootstrap"; val shortName = "bst" }
case class Theming(variant: ThemingVariant = BOOTSTRAP)
