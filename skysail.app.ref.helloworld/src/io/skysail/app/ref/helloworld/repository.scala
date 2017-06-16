package io.skysail.app.ref.helloworld

import io.skysail.repo.orientdb._
import io.skysail.core.domain.repo.ScalaDbRepository

class BookmarksRepository(db: ScalaDbService) extends OrientDbRepository[Hello](db) with ScalaDbRepository {
  db.createWithSuperClass("V", DbClassName.of(classOf[Hello]))
  db.register(classOf[Hello]);
}