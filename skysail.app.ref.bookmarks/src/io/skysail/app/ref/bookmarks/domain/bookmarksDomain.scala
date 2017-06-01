package io.skysail.app.ref.bookmarks.domain

import io.skysail.domain.ddd.ScalaEntity

//@JsonIgnoreProperties(ignoreUnknown = true)
//case class Pact(
//    var id: Option[String] = None,
//    @BeanProperty @(Field @field) var title: String = "",
//    @BeanProperty var turn: Turn = new Turn("test")) extends ScalaEntity[String] {
//
//  // title, selectionStrategy & confirmationS
//
//}
case class Bookmark(title:String,url:String)  extends ScalaEntity[String]
