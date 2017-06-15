package io.skysail.converter.st.wrapper

import org.restlet.Request
import java.util.Collections
import io.skysail.restlet.utils.SortingParamUtils
import org.restlet.Response
import io.skysail.restlet.utils.ScalaHeadersUtils
import org.apache.commons.lang.StringEscapeUtils

case class StResponseWrapper(response: Response) {

  def getHeaders() = StringEscapeUtils.escapeHtml(ScalaHeadersUtils.getHeaders(response).toString())

}