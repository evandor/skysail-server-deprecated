package io.skysail.app.ref.bookmarks.services

import io.skysail.app.ref.bookmarks.BookmarksApplication

object Services {
  def bookmarks = org.restlet.Application.getCurrent().asInstanceOf[BookmarksApplication].bookmarksService
}

