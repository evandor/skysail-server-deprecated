package io.skysail.converter

import io.skysail.api.responses.SkysailResponse
import io.skysail.api.um.UserManagementProvider
import org.restlet.representation.Variant
import io.skysail.restlet.ScalaSkysailServerResource
import io.skysail.api.responses.ListServerResponse
import io.skysail.api.responses.RelationTargetResponse
import io.skysail.api.responses.ConstraintViolationsResponse
import com.fasterxml.jackson.databind.ObjectMapper
import java.util.LinkedHashMap
import io.skysail.restlet.responses.ScalaSkysailResponse
import io.skysail.restlet.app.SkysailApplicationService
import io.skysail.restlet.responses.FormResponse
import java.text.DateFormat
import com.fasterxml.jackson.databind.SerializationFeature
import java.util.Collections

import scala.collection.JavaConverters._
import java.util.HashMap
import java.util.Optional
import io.skysail.domain.core.FieldModel
import io.skysail.restlet.model.ScalaSkysailFieldModel
import io.skysail.converter.forms.helper.CellRendererHelper
import io.skysail.restlet.queries.QueryFilterParser
import io.skysail.restlet.resources.PostEntityServerResource2
import io.skysail.restlet.resources.PutEntityServerResource2
import io.skysail.restlet.forms.ScalaFormField
import io.skysail.api.text.Translation
import io.skysail.restlet.model.resource.StFormFieldsWrapper
import io.skysail.restlet.utils._
import io.skysail.restlet.responses.ListResponse

class ResourceModel(
    resource: ScalaSkysailServerResource,
    response: ScalaSkysailResponse[_],
    userManagementProvider: UserManagementProvider,
    target: Variant,
    theming: Theming) {

  var rawData = new java.util.ArrayList[java.util.Map[String, Object]]()
  def getRawData() = rawData

  var data: java.util.List[java.util.Map[String, Object]] = new java.util.ArrayList[java.util.Map[String, Object]]()
  def getData() = data

  var skysailApplicationService: SkysailApplicationService = null

  val mapper = new ObjectMapper();

  val locale = ScalaResourceUtils.determineLocale(resource);
  val dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, locale);

  var parameterizedType: Class[_] = null
  var fields: Map[String, ScalaFormField] = Map()

  mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
  mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

  //val target = new STTargetWrapper(target);

  val params = if (resource.getQuery() != null) resource.getQuery().getValuesMap() else Collections.emptyMap()

  var filterParser: QueryFilterParser = null
  def setFilterParser(f: QueryFilterParser) = filterParser = f

  //  var messages: java.util.Map[String, Translation]
  //  def setMessages(m: java.util.Map[String,Translation]) = messages = m

  def getResource() = resource

  def process() = {
    rawData = getData(response, resource)
    println(rawData)
    //		if (resource instanceof ListServerResource<?>) {
    //			facets = ((ListServerResource<?>) resource).getFacets();
    //		}
    //
    parameterizedType = resource.getParameterizedType();

    fields = ScalaFormfieldUtils.determineFormfields(response, resource, skysailApplicationService)
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

    data = rawData //convert(entityClassName, identifierName, resource);
    println(data)
    //    		addAssociatedLinks(data);
    //    		addAssociatedLinks(rawData);
  }

  //  private List<Map<String, Object>> getData(Object source, R theResource) {
  def getData(response: Any, theResource: ScalaSkysailServerResource): java.util.ArrayList[java.util.Map[String, Object]] = {
    val result = new java.util.ArrayList[java.util.Map[String, Object]]()
    if (response.isInstanceOf[ListResponse[_]]) {
      //      			List<?> list = ((ListServerResponse<?>) source).getEntity();
      val list = response.asInstanceOf[ListResponse[_]].entity
      for (element <- list) {
        result.add(mapper.convertValue(element, classOf[LinkedHashMap[String, Object]]));
      }

      val p = new java.util.ArrayList[java.util.Map[String, Object]]()
      for (row <- result.asScala) {
        if (row != null) {
          val nR = new java.util.HashMap[String, Object]();
          for (key <- row.keySet().asScala) {
            nR.put(list(0).getClass().getName() + "|" + key, row.get(key));
          }
          p.add(nR);
        }
      }
      return p
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
      println(entity)
      val entityMap = mapper.convertValue(entity, classOf[LinkedHashMap[String, Object]])
      println(entityMap)
      result.add(entityMap);

      val p = new java.util.ArrayList[java.util.Map[String, Object]]();
      for (row <- result.asScala) {
        if (row != null) {
          val nR = new java.util.HashMap[String, Object]()
          for (key <- row.keySet().asScala) {
            nR.put(entity.getClass().getName + "|" + key, row.get(key))
          }
          p.add(nR);
        }
      }

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

  def setSkysailApplicationService(service: SkysailApplicationService) = this.skysailApplicationService = service

  protected def convert(className: String, identifierName: String, resource: ScalaSkysailServerResource): java.util.List[java.util.Map[String, Object]] = {
    val result: java.util.List[java.util.Map[String, Object]] = new java.util.ArrayList[java.util.Map[String, Object]]()
    rawData.asScala
      .filter { d => d != null }
      .foreach(row => {
        val newRow = new HashMap[String, Object]()
        result.add(newRow)
        row.keySet().asScala
          .foreach { columnName =>
            {
              val identifier = row.get(className + "|" + identifierName)
              apply(newRow, row, className, columnName, identifier, resource);
            }
          }
      })
    return result;
  }

  private def apply(newRow: java.util.Map[String, Object], dataRow: java.util.Map[String, Object], className: String, columnName: String,
    id: Any, resource: ScalaSkysailServerResource): Unit = {

    val simpleIdentifier = if (columnName.contains("|")) columnName.split("\\|")(1) else columnName;
    val field = getDomainField(columnName);
    if (field.isPresent()) {
      val value = calc(field.get().asInstanceOf[ScalaSkysailFieldModel], dataRow, columnName, simpleIdentifier, id, resource)
      newRow.put(columnName, value);
    } else if (columnName.endsWith("|id")) {
      newRow.put(columnName, dataRow.get(columnName));
    } else {
    }
  }

  def getDomainField(columnName: String): Optional[FieldModel] = {
    val applicationModel = resource.getSkysailApplication().getApplicationModel();
    val entity = applicationModel.getEntity(parameterizedType.getName());
    if (entity == null) Optional.empty() else Optional.ofNullable(entity.getField(columnName));
  }

  //  private String calc(@NonNull SkysailFieldModel field, Map<String, Object> dataRow, String columnName,
  //			String simpleIdentifier, Object id, R resource) {
  //		return new CellRendererHelper(field, response, filterParser).render(dataRow.get(columnName), columnName,
  //				simpleIdentifier, id, resource);
  //	}
  def calc(
    fieldModel: ScalaSkysailFieldModel,
    dataRow: java.util.Map[String, Object],
    columnName: String,
    simpleIdentifier: String,
    id: Any,
    resource: ScalaSkysailServerResource) = {
    new CellRendererHelper(fieldModel, response, filterParser)
    //.render(dataRow.get(columnName), columnName, simpleIdentifier, id, resource);
  }

  def isForm() = response.isForm()
  def isPostEntityServerResource() = resource.isInstanceOf[PostEntityServerResource2[_]]
  def isPutEntityServerResource() = resource.isInstanceOf[PutEntityServerResource2[_]]

  def getFormfields() = fields.values.asJava

  def getFormfieldsWrapper(): StFormFieldsWrapper = new StFormFieldsWrapper(fields.values, this.resource.getRequest(), Map())

  def isList() = response.isList()

  def getLinks() = resource.getAuthorizedLinks()

}