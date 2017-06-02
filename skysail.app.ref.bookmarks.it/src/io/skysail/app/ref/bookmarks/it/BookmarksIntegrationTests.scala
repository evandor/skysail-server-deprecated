package io.skysail.app.ref.bookmarks.it

import io.skysail.testsupport.BrowserTests2
import java.math.BigInteger
import org.assertj.core.api.Assertions._
import org.restlet.data._
import org.json.JSONObject
import org.json4s._
import org.json4s.native.JsonMethods._
import org.junit._
import io.skysail.app.ref.bookmarks.domain.Bookmark

class BookmarksIntegrationTests extends BrowserTests2[BookmarksBrowser] {

  private implicit val formats = DefaultFormats

  @Before def setUp() {
    browser = new BookmarksBrowser(2018)
  }

//  //@Test
//  def postPact_returns_created_pact_with_id() {
//    val title = "pactTitle_" + new BigInteger(130, random).toString(32)
//    val pact = browser.postToPostPacts(Pact(None, title))
//    assertThat(pact.id.get).isNotNull()
//    assertThat(pact.title).isEqualTo(title)
//  }
//
  @Test
  def postBookmark_returns_created_bookmark_with_id() {
    val model = "bookmarkTitle_" + new BigInteger(130, random).toString(32)
    val bookmark = browser.postToPostBookmark(Bookmark(None, "title", "url"))
    assertThat(bookmark.id.isDefined).isTrue()
    //assertThat(car.model).isEqualTo(model)
  }
//
//  //@Test
//  def postPact_returns_created_pact_with_default_turn() {
//    val pact = browser.postToPostPacts(Pact(None, "pactTitle_" + new BigInteger(130, random).toString(32)))
//    assertThat(pact.getTurn()).isNotNull()
//    assertThat(pact.getTurn().nextTurn).isEqualTo("test")
//  }

  //@Test
  def pacts_are_available_in_various_formats() {
    var rep = browser.getPacts().getText
    assertThat(rep).startsWith("{")
    assertThat(rep).endsWith("}")

    rep = browser.getPacts(MediaType.TEXT_XML).getText
    assertThat(rep).startsWith("<")
    assertThat(rep).endsWith(">")

    rep = browser.getPacts(MediaType.TEXT_HTML).getText
    assertThat(rep).contains("<!DOCTYPE html>")
  }

  //@Test
  def get_request_on_PostPactEndpoint_with_html_media_type_returns_HTML() {
    val rep = browser.getPostPacts(MediaType.TEXT_HTML).getText
    assertThat(rep).contains("<!DOCTYPE html>")
  }


}