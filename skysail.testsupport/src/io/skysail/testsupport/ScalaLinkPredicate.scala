package io.skysail.testsupport

import org.restlet.util.Series
import org.restlet.data.Method
import org.restlet.data.Header
import com.google.common.base.Predicate
import io.skysail.core.model.LinkModel
import org.slf4j.LoggerFactory
import ScalaApplicationClient.{ TESTTAG => logPrefix }

abstract class ScalaLinkPredicate(series: Series[Header]) extends Predicate[LinkModel] {
  protected val log = LoggerFactory.getLogger(this.getClass())
  val link = series.getFirstValue("Link")
  override def toString() = "\n - " + link.replace(",", "\n - ")
}

class ScalaLinkTitlePredicate(val title: String, val series: Series[Header]) extends ScalaLinkPredicate(series) {
  def apply(l: LinkModel): Boolean = {
    log.debug(s"$logPrefix comparing '${l.getTitle()}' with '${title}' [exact]")
    l.getTitle().equals(title)
  }
}

class ScalaLinkSubTitlePredicate(title: String, series: Series[Header]) extends ScalaLinkTitlePredicate(title, series) {
  override def apply(l: LinkModel): Boolean = {
    log.debug(s"$logPrefix comparing '${l.getTitle()}' with '${title}' [contains lowercase]")
    l.getTitle().toLowerCase().contains(title.toLowerCase())
  }
}

class ScalaLinkMethodPredicate(method: Method, series: Series[Header]) extends ScalaLinkPredicate(series) {
  def apply(l: LinkModel): Boolean = true //l.getVerbs().contains(method)
}
