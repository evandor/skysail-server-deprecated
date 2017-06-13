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

  private var bookmark: Bookmark = null

  @Before def setUp() {
    browser = new BookmarksBrowser(2018)
    bookmark = createSomeBookmark()
  }

  @Test
  def bookmarks_are_available_in_various_formats() {
    var rep = browser.getBookmarks().getText
    assertThat(rep).startsWith("{")
    assertThat(rep).endsWith("}")

    rep = browser.getBookmarks(MediaType.TEXT_XML).getText
    assertThat(rep).startsWith("<")
    assertThat(rep).endsWith(">")

    rep = browser.getBookmarks(MediaType.TEXT_HTML).getText
    assertThat(rep).contains("<!DOCTYPE html>")
  }

  @Test
  def postBookmark_returns_created_bookmark_with_id() {
    val entity = browser.postToPostBookmark(bookmark)
    assertThat(entity.id.isDefined).isTrue()
    assertThat(entity.title).isEqualTo(bookmark.title)
  }

  @Test
  def created_bookmark_shows_in_bookmarksList() {
    val entity = browser.postToPostBookmark(bookmark)
    val bookmarks = browser.getBookmarks()
    //println(bookmarks.getText)
    assertThat(bookmarks.getText).contains(entity.title)
  }

  @Test
  def retrieve_created_bookmark() {
    val entity = browser.postToPostBookmark(bookmark)
    //browser.getBookmarks()
  }

  def createSomeBookmark() = {
    val title = "bookmarkTitle_" + new BigInteger(130, random).toString(32)
    Bookmark(None, title, "url")
  }

}