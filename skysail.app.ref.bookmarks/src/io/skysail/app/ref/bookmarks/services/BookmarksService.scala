package io.skysail.app.ref.bookmarks.services

import io.skysail.repo.orientdb.ScalaDbService
import io.skysail.core.model.ApplicationModel
import org.json4s.DefaultFormats
import scala.util.Try
import io.skysail.queryfilter.filter.Filter
import io.skysail.queryfilter.pagination.Pagination
import io.skysail.app.ref.bookmarks.repository.BookmarksRepository
import io.skysail.app.ref.bookmarks.domain.Bookmark

class BookmarksService(dbService: ScalaDbService, appModel: ApplicationModel) {

  private var repo = new BookmarksRepository(dbService)
  private implicit val formats = DefaultFormats

  private var i = 0

  def create(car: Bookmark): Try[Bookmark] = repo.save(car, appModel)
  def update(car: Bookmark): Try[Bookmark] = repo.save(car, appModel)

  def getById(id: String): Option[Bookmark] = {
    val entry = repo.findOne(id)
    if (entry.isDefined) Some(entry.get.extract[Bookmark]) else None
  }

  def find(f: Filter, p: Pagination) = repo.find(f, p).map {
    row =>
      {
        //println(row)
        row.extract[Bookmark]
      }
  }.toList

  //
  //  def findOne(id: String): Option[Connection] = {
  //    val option = repo.findOne(id)
  //    if (option.isDefined) Some(option.get.extract[Connection]) else None
  //  }
  //
  //  def save(entity: Connection): Connection = {
  //    val vertex = repo.save(entity, appModel)
  //    // entity.setId(vertex.getId().toString())
  //    entity.copy(id = Some(vertex.get.id.toString()))
  //  }

}