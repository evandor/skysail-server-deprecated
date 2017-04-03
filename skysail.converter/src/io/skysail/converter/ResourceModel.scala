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

class ResourceModel(
    resource: ScalaSkysailServerResource, 
    response: ScalaSkysailResponse[_], 
    userManagementProvider: UserManagementProvider, 
    target: Variant, theming: Theming) {

  var rawData = new java.util.ArrayList[java.util.Map[String, Object]]()
  
  var skysailApplicationService: ScalaSkysailApplicationService = null

  val mapper = new ObjectMapper();

  def process() = {
    rawData = getData(response, resource);

    //		if (resource instanceof ListServerResource<?>) {
    //			facets = ((ListServerResource<?>) resource).getFacets();
    //		}
    //
    val parameterizedType = resource.getParameterizedType();

    val fields = FormfieldUtils.determineFormfields(response, resource, skysailApplicationService)
    println(fields)
    
//    		rootEntity = new EntityModel<>(response.getEntity(), resource);
//    
//    		String identifierName = getIdentifierFormField(rawData);
//    		SkysailResponse ssr = response;
//    		String entityClassName = ssr.getEntity() != null ? ssr.getEntity().getClass().getName() : "";
//    		if (ssr.getEntity() != null && List.class.isAssignableFrom(ssr.getEntity().getClass())) {
//    			List listEntity = (List) ssr.getEntity();
//    			if (listEntity.size() > 0) {
//    				entityClassName = listEntity.get(0).getClass().getName();
//    			}
//    		}
//    
//    		data = convert(entityClassName, identifierName, resource);
//    
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

  def setSkysailApplicationService(service: ScalaSkysailApplicationService) = {
      this.skysailApplicationService = service
    }
}