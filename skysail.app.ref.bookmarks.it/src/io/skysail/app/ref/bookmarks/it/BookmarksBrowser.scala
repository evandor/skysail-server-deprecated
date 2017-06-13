package io.skysail.app.ref.bookmarks.it

import org.restlet.data._
import org.restlet.ext.json.JsonRepresentation
import org.json.JSONObject
import io.skysail.testsupport._
import io.skysail.app.ref.bookmarks.domain._
import java.math.BigInteger
import java.util.Random
import org.slf4j.LoggerFactory
import org.restlet.representation.Representation
import io.skysail.testsupport.PathDsl._
import org.json4s._
import org.json4s.native.JsonMethods._

import ScalaApplicationClient.{ TESTTAG => logPrefix }
import org.json4s.DefaultFormats

class BookmarksBrowser(port: Integer) extends ScalaApplicationBrowser("bookmarks", port) {

  private val log = LoggerFactory.getLogger(this.getClass())
  private val random = new Random()
  private implicit val formats = DefaultFormats

  def getBookmarks(mediaType: MediaType = MediaType.APPLICATION_JSON) = {
    log.info(s"$logPrefix getting bookmarks")
    client.get("/" --> appName, mediaType)
  }

//  def getPostPacts(mediaType: MediaType = MediaType.APPLICATION_JSON): Representation = {
//    log.info(s"$logPrefix getting pacts")
//    client.get("/" --> appName --> "post pact", mediaType)
//  }

  def postToPostBookmark(bookmark: Bookmark, mediaType: MediaType = MediaType.APPLICATION_JSON) = {
    log.info(s"$logPrefix posting form to bookmarks")
    val rep = client.post(createForm(bookmark), "/" --> appName --> "post bookmark", mediaType).getText
    parse(rep).extract[Bookmark]
  }


  def getNextTurn(mediaType: MediaType = MediaType.APPLICATION_JSON): Representation = {
    log.info(s"$logPrefix getting next turn")
    getTurn()
  }

//  def createPact() = {
//    log.info(s"$logPrefix posting new pact")
//    createWithForm(client, Pact(None, "test pact"))
//  }

  def createRandomEntity(): JSONObject = {
    val jo = new JSONObject()
    try {
      jo.put("content", "note_" + new BigInteger(130, random).toString(32))
      //jo.put("iban", "DE00000000000000000000")
    } catch {
      case _: Any =>
      //log.error(e.getMessage(),e)
    }
    return jo
  }

  override def loginAs(username: String, password: String): BookmarksBrowser = {
    super.loginAs(username, password)
    this
  }

  def create(entity: JSONObject) = {
    log.info(s"sometag: creating new AnEntity $entity")
    // login()
    createEntity(client, entity)
  }

  def createForm(x$1: String): Form = {
    ???
  }

  private def createEntity(client: ScalaApplicationClient, entity: JSONObject) = {
    navigateToPostEntityPage(client)
    // client.post(createForm(entity))
    client.post(new JsonRepresentation(entity), MediaType.APPLICATION_JSON)
    setId(client.getLocation().getLastSegment(true))
  }

  private def getTurn() = {
    client.gotoAppRoot().followLinkTitle(Method.GET,"", "turn")
    client.currentRepresentation
  }

  private def navigateToPostEntityPage(client: ScalaApplicationClient) {
    client.gotoAppRoot().followLinkTitle(Method.GET,"", "post confirmation")
  }

  private def navigateToPostPactPage(client: ScalaApplicationClient) {
    client.gotoAppRoot().followLinkTitle(Method.GET,"", "post pact")
  }
  
  private def createForm(bookmark: Bookmark) = {
    val form = new Form()
    form.add(classOf[Bookmark].getName + "|title", bookmark.title)
    form.add(classOf[Bookmark].getName + "|url", bookmark.url)
    form
  }


}