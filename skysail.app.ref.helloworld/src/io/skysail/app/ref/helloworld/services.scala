package io.skysail.app.ref.helloworld

import io.skysail.repo.orientdb.ScalaDbService
import io.skysail.core.model.ApplicationModel
import io.skysail.queryfilter.pagination.Pagination
import io.skysail.queryfilter.filter.Filter
import scala.util.Try
import org.json4s.DefaultFormats

object Services {
  def hellosService = org.restlet.Application.getCurrent().asInstanceOf[HelloWorldApplication].hellosService
}

class HelloWorldService(dbService: ScalaDbService, appModel: ApplicationModel) {

  private var repo = new BookmarksRepository(dbService)
  private implicit val formats = DefaultFormats

  private var i = 0

  def create(car: Hello): Try[Hello] = repo.save(car, appModel)
  def update(car: Hello): Try[Hello] = repo.update(car, appModel)

  def getById(id: String): Option[Hello] = {
    val entry = repo.findOne(id)
    if (entry.isDefined) Some(entry.get.extract[Hello]) else None
  }

  def find(f: Filter, p: Pagination) = repo.find(f, p).map { row => row.extract[Hello] }.toList

}