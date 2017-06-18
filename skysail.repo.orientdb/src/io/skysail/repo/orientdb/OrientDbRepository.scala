package io.skysail.repo.orientdb

import scala.util.Try
import scala.util._
import scala.collection.JavaConverters._
import org.json4s.JsonAST.JValue
import io.skysail.core.restlet.utils.ScalaReflectionUtils
import io.skysail.queryfilter.filter.Filter
import io.skysail.queryfilter.pagination.Pagination
import io.skysail.core.model.ApplicationModel
import io.skysail.core.domain.ScalaEntity
import io.skysail.core.domain.repo.ScalaDbRepository

trait BaseDbRepository[T] extends ScalaDbRepository {
  def save(entity: T, appModel: ApplicationModel): Try[T]
  def findOne(id: String): Option[JValue]
}

class OrientDbRepository[T](db: ScalaDbService) extends BaseDbRepository[T] {

  val entityType = ScalaReflectionUtils.getParameterizedType(getClass());

  def find(filter: Filter, pagination: Pagination): List[JValue] = {
    val sql = "SELECT * from " + DbClassName.of(entityType)
    //                + (!org.restlet.engine.util.StringUtils.isNullOrEmpty(filter.getPreparedStatement()) ? " WHERE " + filter.getPreparedStatement()
    //                        : "")
    //                + " " + limitClause(pagination);
    //        pagination.setEntityCount(count(filter));
    db.findGraphs[T](entityType, sql, filter.params)
  }

  def findOne(id: String): Option[JValue] = db.findOne(id)
  
  def update(entity: T, appModel: ApplicationModel): Try[T] = {
    val result = db.update(entity, appModel)
    if (result.isSuccess) {
      Success(entity)
    } else {
      Failure(result.failed.get)
    }
  }

  def save(entity: T, appModel: ApplicationModel): Try[T] = {
    val result = db.persist(entity, appModel)
    //result.transform(s => s.asInstanceOf[T], f)
    if (result.isSuccess) {
      if (entity.isInstanceOf[ScalaEntity[_]]) {
        val idAsString = result.get.getId.toString()
        entity.asInstanceOf[ScalaEntity[String]].id = Some(idAsString)
      }
      return Success(entity)
    } else {
      Failure(result.failed.get)
    }
  }
}