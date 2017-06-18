package io.skysail.converter.st.wrapper

import org.restlet.Request
import java.util.Collections
import io.skysail.core.restlet.utils.SortingParamUtils

case class StRequestWrapper(request: Request, fieldNames: List[String]) {

  class RequestAdapter(request: Request) {
    var sortIndicators = Map[String, String]()
    var toggleSortLinks = Map[String, String]()
    var context = "/";
    val hierarchicalPart = request.getOriginalRef().getHierarchicalPart();
    val query = Collections.unmodifiableList(request.getOriginalRef().getQueryAsForm());
    val segments = request.getOriginalRef().getSegments();
    if (segments != null && segments.size() > 1) {
      context = "/" + segments.get(0) + "/" + segments.get(1);
    }
  }

  val adapter = new RequestAdapter(request);
  
  def getRequest() = request
  
  //adapter.sortIndicators = fieldNames.map(name => name -> new SortingParamUtils(name, request).getSortIndicator())
  //adapter.toggleSortLinks = fieldNames.map(name => name -> new SortingParamUtils(name, request).toggleSortLink())

}