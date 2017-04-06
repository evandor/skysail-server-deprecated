package io.skysail.repo.orientdb

import io.skysail.domain.core.ScalaDbRepository
import io.skysail.server.queryfilter.filtering.Filter
import io.skysail.server.queryfilter.pagination.Pagination
import io.skysail.core.utils.ReflectionUtils
import io.skysail.repo.orientdb.helper.DbClassName

class OrientDbRepository[T](db: DbService) extends ScalaDbRepository {
  
  val entityType = ReflectionUtils.getParameterizedType(getClass());
  
   def find(filter: Filter , pagination: Pagination ):List[T] = {
        val sql = "SELECT * from " + DbClassName.of(entityType)
//                + (!org.restlet.engine.util.StringUtils.isNullOrEmpty(filter.getPreparedStatement()) ? " WHERE " + filter.getPreparedStatement()
//                        : "")
//                + " " + limitClause(pagination);
//        pagination.setEntityCount(count(filter));
//        dbService.findGraphs(entityType, sql, filter.getParams());
        null
    }
   
   def findOne(id: String):Option[T] = {
     None
   }
}