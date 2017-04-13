package io.skysail.queryfilter.filter

import org.restlet.Request

class Filter(request:Request, defaultFilterExpression: String) {
  
  def this(request:Request) = this(request,"")
  val params  = Map[String, Object]();
}