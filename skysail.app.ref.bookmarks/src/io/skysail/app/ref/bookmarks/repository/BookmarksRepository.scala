package io.skysail.app.ref.bookmarks.repository

import io.skysail.domain.repo.ScalaDbRepository;
import io.skysail.repo.orientdb._
import io.skysail.app.ref.bookmarks.domain.Bookmark

class BookmarksRepository(db: ScalaDbService) extends OrientDbRepository[Bookmark](db) with ScalaDbRepository {

  db.createWithSuperClass("V", DbClassName.of(classOf[Bookmark]))
  db.register(classOf[Bookmark]);
}