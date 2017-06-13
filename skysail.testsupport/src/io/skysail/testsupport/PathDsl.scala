package io.skysail.testsupport

import org.restlet.data.MediaType
import org.restlet.representation.StringRepresentation
import org.restlet.representation.Representation

object PathDsl {

  sealed trait Method {
    def -->(s: String): RouteDef0 = { stringToRouteDef0("hi") }
  }
  case object ANY extends Method
  case object GET extends Method
  case object POST extends Method

  sealed trait PathElem {
    def name: String
  }
  case class Static(name: String) extends PathElem
  //case object * extends PathElem

  sealed trait RouteDef[Self] {

    //def withMethod(method: Method): Self
    //def method: Method
    def elems: List[PathElem]
    var url: String = _
  }

  implicit def stringToRouteDef0(name: String) = RouteDef0(ANY, Static(name) :: Nil)

  case class RouteDef0(method: Method, elems: List[PathElem]) extends RouteDef[RouteDef0] {
    def -->(s:String):RouteDef1 = RouteDef1(ANY, elems :+ Static(s))
  }
  case class RouteDef1(method: Method, elems: List[PathElem]) extends RouteDef[RouteDef1] {
    def -->(s:String):RouteDef2 = RouteDef2(ANY, elems :+ Static(s))    
    def get(mediaType: MediaType): Representation = { new StringRepresentation("hi")}
  }
  case class RouteDef2(method: Method, elems: List[PathElem]) extends RouteDef[RouteDef2] {
    //def -->(s:String):RouteDef3 = RouteDef2(ANY, elems :+ Static(s))    
    def get(mediaType: MediaType): Representation = { new StringRepresentation("hi")}
  }
}