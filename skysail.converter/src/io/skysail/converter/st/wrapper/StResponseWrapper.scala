package io.skysail.converter.st.wrapper

import org.restlet.Request
import java.util.Collections
import io.skysail.core.restlet.utils.SortingParamUtils
import org.restlet.Response
import io.skysail.core.restlet.utils.ScalaHeadersUtils
import org.apache.commons.lang.StringEscapeUtils

case class StResponseWrapper(response: Response) {

  def getHeaders() = StringEscapeUtils.escapeHtml(ScalaHeadersUtils.getHeaders(response).toString())

}