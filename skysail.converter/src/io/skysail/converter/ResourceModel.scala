package io.skysail.converter

import io.skysail.api.responses.SkysailResponse
import io.skysail.api.um.UserManagementProvider
import io.skysail.server.rendering.Theme
import org.restlet.representation.Variant
import io.skysail.restlet.ScalaSkysailServerResource
import io.skysail.api.responses.ListServerResponse
import io.skysail.api.responses.RelationTargetResponse
import io.skysail.api.responses.ConstraintViolationsResponse
import com.fasterxml.jackson.databind.ObjectMapper
import java.util.LinkedHashMap
import io.skysail.restlet.utils.FormfieldUtils
import io.skysail.core.app.SkysailApplicationService
import io.skysail.restlet.responses.ScalaSkysailResponse
import io.skysail.restlet.app.ScalaSkysailApplicationService
import io.skysail.restlet.responses.FormResponse
import io.skysail.restlet.utils.ResourceUtils
import java.text.DateFormat
import com.fasterxml.jackson.databind.SerializationFeature
import java.util.Collections

class ResourceModel(
    resource: ScalaSkysailServerResource,
    response: ScalaSkysailResponse[_],
    userManagementProvider: UserManagementProvider,
    target: Variant,
    theming: Theming) {

  var rawData = new java.util.ArrayList[java.util.Map[String, Object]]()

  var skysailApplicationService: ScalaSkysailApplicationService = null

  val mapper = new ObjectMapper();

  val locale = ResourceUtils.determineLocale(resource);
  val dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, locale);

  mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
  mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

  //val target = new STTargetWrapper(target);

  val params = if (resource.getQuery() != null) resource.getQuery().getValuesMap() else Collections.emptyMap()

  def process() = {
    rawData = getData(response, resource);

    //		if (resource instanceof ListServerResource<?>) {
    //			facets = ((ListServerResource<?>) resource).getFacets();
    //		}
    //
    val parameterizedType = resource.getParameterizedType();

    val fields = FormfieldUtils.determineFormfields(response, resource, skysailApplicationService)
    println(fields)

   // val rootEntity = new io.skysail.server.model.EntityModel[_](response.entity(), resource);
        
    val identifierName = "id" //getIdentifierFormField(rawData);
    //    		SkysailResponse ssr = response;
    var entityClassName = if (response.entity != null) response.entity.getClass().getName() else ""
  	if (response.entity != null && classOf[java.util.List[_]].isAssignableFrom(response.entity.getClass())) {
  		val listEntity = response.entity.asInstanceOf[java.util.List[_]]
  		if (listEntity.size() > 0) {
  			entityClassName = listEntity.get(0).getClass().getName();
  		}
  	}
        
    val data = convert(entityClassName, identifierName, resource);
        
    //    		addAssociatedLinks(data);
    //    		addAssociatedLinks(rawData);
  }

  //  private List<Map<String, Object>> getData(Object source, R theResource) {
  def getData(response: Any, theResource: ScalaSkysailServerResource): java.util.ArrayList[java.util.Map[String, Object]] = {
    val result = new java.util.ArrayList[java.util.Map[String, Object]]()
    if (response.isInstanceOf[ListServerResponse[_]]) {
      //			List<?> list = ((ListServerResponse<?>) source).getEntity();
      //			if (list != null) {
      //				for (Object object : list) {
      //					result.add(mapper.convertValue(object, LinkedHashMap.class));
      //				}
      //
      //				List<Map<String, Object>> p = new ArrayList<>();
      //				for (Map<String, Object> row : result) {
      //					if (row != null) {
      //						Map<String, Object> nR = new HashMap<>();
      //						for (String key : row.keySet()) {
      //							nR.put(list.get(0).getClass().getName() + "|" + key, row.get(key));
      //						}
      //						p.add(nR);
      //					}
      //				}
      //
      //				return p;
      //			}
    } else if (response.isInstanceOf[RelationTargetResponse[_]]) {
      //			List<?> list = ((RelationTargetResponse<?>) source).getEntity();
      //			if (list != null) {
      //				for (Object object : list) {
      //					result.add(mapper.convertValue(object, LinkedHashMap.class));
      //				}
      //			}
      //		} else if (source instanceof EntityServerResponse) {
      //			Object entity = ((EntityServerResponse<?>) source).getEntity();
      //			result.add(mapper.convertValue(entity, LinkedHashMap.class));
      //
      //			List<Map<String, Object>> p = new ArrayList<>();
      //			for (Map<String, Object> row : result) {
      //				if (row != null) {
      //					Map<String, Object> nR = new HashMap<>();
      //					for (String key : row.keySet()) {
      //						nR.put(entity.getClass().getName() + "|" + key, row.get(key));
      //					}
      //					p.add(nR);
      //				}
      //			}
      //
      //			return p;
      //
    } else if (response.isInstanceOf[FormResponse[_]]) {
      val entity = response.asInstanceOf[FormResponse[_]].entity
      result.add(mapper.convertValue(entity, classOf[LinkedHashMap[String, Object]]));

      val p = new java.util.ArrayList[java.util.Map[String, Object]]();
      /*for (row <- result) {
      				if (row != null) {
      					Map<String, Object> nR = new HashMap<>();
      					for (String key : row.keySet()) {
      						nR.put(entity.getClass().getName() + "|" + key, row.get(key));
      					}
      					p.add(nR);
      				}
      			}*/

      return p;

    } else if (response.isInstanceOf[ConstraintViolationsResponse[_]]) {
      //			Object entity = ((ConstraintViolationsResponse<?>) source).getEntity();
      //			result.add(mapper.convertValue(entity, LinkedHashMap.class));
    }

    // for (Map<String, Object> row : result) {
    // row.entrySet().stream().collect(Collectors.toMap(e ->
    // source.getClass().getName() + "|" + e.getKey(), e->e.getValue()));
    // }
    //
    return result;
  }

  def setSkysailApplicationService(service: ScalaSkysailApplicationService) = this.skysailApplicationService = service
  
  	protected def convert( className: String, identifierName: String, resource: ScalaSkysailResponse[_]): java.util.List[Map[String, Object]] = {
		val result: java.util.List[Map[String, Object]] = new java.util.ArrayList[Map[String, Object]]()
//		rawData.stream().filter(row -> row != null).forEach(row -> {
//			Map<String, Object> newRow = new HashMap<>();
//			result.add(newRow);
//			row.keySet().stream().forEach(columnName -> { // e.g.
//															// io.skysail.server.app.ref.singleentity.Account|owner
//				Object identifier = row.get(className + "|" + identifierName); // e.g.
//																				// io.skysail.server.app.ref.singleentity.Account|id
//				if (identifier != null) {
//					apply(newRow, row, className, columnName, identifier, resource);
//				} else {
//					// for now, for Gatling(?)
//					log.debug("identifier was null");
//					apply(newRow, row, className, columnName, "", resource);
//				}
//			});
//		});
		return result;
	}


}