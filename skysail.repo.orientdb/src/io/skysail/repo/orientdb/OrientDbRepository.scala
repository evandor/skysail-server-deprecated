package io.skysail.repo.orientdb

import io.skysail.server.queryfilter.filtering.Filter
import io.skysail.server.queryfilter.pagination.Pagination
import io.skysail.core.utils.ReflectionUtils
import scala.util.Try
import io.skysail.domain.repo.ScalaDbRepository
import io.skysail.restlet.model.ScalaSkysailApplicationModel
import scala.util._
import io.skysail.domain.ddd.ScalaEntity
import scala.collection.JavaConverters._
import org.json4s.JsonAST.JValue

trait BaseDbRepository[T] extends ScalaDbRepository {
  def save(entity: T, appModel: ScalaSkysailApplicationModel): Try[T]
  def findOne(id: String): Option[T]
}

class OrientDbRepository[T](db: ScalaDbService) extends BaseDbRepository[T] {

  val entityType = ReflectionUtils.getParameterizedType(getClass());

  def find(filter: Filter, pagination: Pagination): List[JValue] = {
    val sql = "SELECT * from " + DbClassName.of(entityType)
    //                + (!org.restlet.engine.util.StringUtils.isNullOrEmpty(filter.getPreparedStatement()) ? " WHERE " + filter.getPreparedStatement()
    //                        : "")
    //                + " " + limitClause(pagination);
    //        pagination.setEntityCount(count(filter));
    db.findGraphs[T](entityType, sql, filter.getParams().asScala.toMap)
  }

  def findOne(id: String): Option[T] = {
    None
  }

  def save(entity: T, appModel: ScalaSkysailApplicationModel): Try[T] = {
    val result = db.persist(entity, appModel)
    //result.transform(s => s.asInstanceOf[T], f)
    if (result.isSuccess) {
      if (entity.isInstanceOf[ScalaEntity[String]]) {
        val idAsString = result.get.getId.toString()
        entity.asInstanceOf[ScalaEntity[String]].id = Some(idAsString)
      }
      return Success(entity)
    } else {
      Failure(result.failed.get)
    }
  }
}