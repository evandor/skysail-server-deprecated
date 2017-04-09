package io.skysail.repo.orientdb

import io.skysail.server.queryfilter.filtering.Filter
import io.skysail.server.queryfilter.pagination.Pagination
import io.skysail.core.utils.ReflectionUtils
import scala.util.Try
import io.skysail.domain.repo.ScalaDbRepository
import io.skysail.restlet.model.ScalaSkysailApplicationModel
import scala.util.Failure

trait BaseDbRepository[T] extends ScalaDbRepository {
  def save(entity: T, appModel: ScalaSkysailApplicationModel): Try[T]
  def findOne(id: String): Option[T]
}

class OrientDbRepository[T](db: ScalaDbService) extends BaseDbRepository[T] {

  val entityType = ReflectionUtils.getParameterizedType(getClass());

  def find(filter: Filter, pagination: Pagination): List[T] = {
    val sql = "SELECT * from " + DbClassName.of(entityType)
    //                + (!org.restlet.engine.util.StringUtils.isNullOrEmpty(filter.getPreparedStatement()) ? " WHERE " + filter.getPreparedStatement()
    //                        : "")
    //                + " " + limitClause(pagination);
    //        pagination.setEntityCount(count(filter));
    //        dbService.findGraphs(entityType, sql, filter.getParams());
    null
  }

  def findOne(id: String): Option[T] = {
    None
  }

  def save(entity: T, appModel: ScalaSkysailApplicationModel): Try[T] = {
    db.persist(entity, appModel)
    Failure(new Exception(""))
  }
}