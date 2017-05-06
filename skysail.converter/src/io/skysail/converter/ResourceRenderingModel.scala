package io.skysail.converter

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import io.skysail.api.responses.SkysailResponse
import io.skysail.api.um.UserManagementProvider
import io.skysail.restlet.SkysailServerResource
import io.skysail.api.responses.ListServerResponse
import io.skysail.api.responses.RelationTargetResponse
import io.skysail.api.responses.ConstraintViolationsResponse
import io.skysail.restlet.responses.ScalaSkysailResponse
import io.skysail.restlet.app.SkysailApplicationService
import io.skysail.restlet.responses.FormResponse
import io.skysail.api.text.Translation
import io.skysail.api.links._
import io.skysail.converter.forms.helper.CellRendererHelper
import io.skysail.core.model.resource.StFormFieldsWrapper
import io.skysail.core.model._
import io.skysail.restlet.queries.QueryFilterParser
import io.skysail.restlet.resources._
import io.skysail.restlet.forms.ScalaFormField
import io.skysail.restlet.utils._
import io.skysail.restlet.responses._
import java.util.LinkedHashMap
import java.util.HashMap
import java.util.Optional
import java.text.DateFormat
import java.util.Collections
import org.restlet.representation.Variant
import org.restlet.data.MediaType
import scala.collection.JavaConverters._
import io.skysail.restlet.services.MenuItemProvider

object ResourceRenderingModel {
  val ID = "id";
}

class ResourceRenderingModel(
    resource: SkysailServerResource[_],
    response: ScalaSkysailResponse[_],
    userManagementProvider: UserManagementProvider,
    target: Variant,
    theming: Theming) {

  val appModel = resource.getSkysailApplication().getApplicationModel2()
  def getAppModelHtmlRepresentation() = appModel.toHtml(resource.getRequest)

  var menuProviders = Set[MenuItemProvider]()
  def setMenuProviders(p: Set[MenuItemProvider]) = menuProviders = p

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
    //println(rawData)
    //		if (resource instanceof ListServerResource<?>) {
    //			facets = ((ListServerResource<?>) resource).getFacets();
    //		}
    //
    parameterizedType = resource.getParameterizedType();

    fields = ScalaFormfieldUtils.determineFormfields(response, resource)

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
    //println(data)
    addAssociatedLinks(data.asScala.toList);
    addAssociatedLinks(rawData.asScala.toList);
  }

  //  private List<Map<String, Object>> getData(Object source, R theResource) {
  def getData(response: Any, theResource: SkysailServerResource[_]): java.util.ArrayList[java.util.Map[String, Object]] = {
    val result = new java.util.ArrayList[java.util.Map[String, Object]]()
    if (response.isInstanceOf[ListResponse[_]]) {
      //      			List<?> list = ((ListServerResponse<?>) source).getEntity();
      val list = response.asInstanceOf[ListResponse[_]].entity
      for (element <- list) {
        result.add(mapper.convertValue(element, classOf[LinkedHashMap[String, Object]]));
      }

      return adjustKeyNames(result, list(0).getClass())

      /*val p = new java.util.ArrayList[java.util.Map[String, Object]]()
      for (row <- result.asScala) {
        if (row != null) {
          val nR = new java.util.HashMap[String, Object]();
          for (key <- row.keySet().asScala) {
            nR.put(list(0).getClass().getName() + "|" + key, row.get(key));
          }
          p.add(nR);
        }
      }
      return p*/
    } else if (response.isInstanceOf[EntityResponse[_]]) {
      val entity = response.asInstanceOf[EntityResponse[_]].entity.asInstanceOf[Option[_]]
      result.add(mapper.convertValue(entity.get, classOf[LinkedHashMap[String, Object]]))
      return adjustKeyNames(result, entity.get.getClass())
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
      //println(entity)
      val entityMap = mapper.convertValue(entity, classOf[LinkedHashMap[String, Object]])
      //println(entityMap)
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

  protected def convert(className: String, identifierName: String, resource: SkysailServerResource[_]): java.util.List[java.util.Map[String, Object]] = {
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
    id: Any, resource: SkysailServerResource[_]): Unit = {

    val simpleIdentifier = if (columnName.contains("|")) columnName.split("\\|")(1) else columnName;
    //    val field = getDomainField(columnName);
    //    if (field.isPresent()) {
    //      val value = calc(field.get().asInstanceOf[ScalaSkysailFieldModel], dataRow, columnName, simpleIdentifier, id, resource)
    //      newRow.put(columnName, value);
    //    } else if (columnName.endsWith("|id")) {
    newRow.put(columnName, dataRow.get(columnName));
    //    } else {
    //    }
  }

  //  private def getDomainField(columnName: String): Optional[FieldModel] = {
  //    //val applicationModel = resource.getSkysailApplication().getApplicationModel();
  //    val entity = applicationModel.getEntity(parameterizedType.getName());
  //    if (entity == null) Optional.empty() else Optional.ofNullable(entity.getField(columnName));
  //  }

  //  private String calc(@NonNull SkysailFieldModel field, Map<String, Object> dataRow, String columnName,
  //			String simpleIdentifier, Object id, R resource) {
  //		return new CellRendererHelper(field, response, filterParser).render(dataRow.get(columnName), columnName,
  //				simpleIdentifier, id, resource);
  //	}
  def calc(
    fieldModel: FieldModel,
    dataRow: java.util.Map[String, Object],
    columnName: String,
    simpleIdentifier: String,
    id: Any,
    resource: SkysailServerResource[_]) = {
    new CellRendererHelper(fieldModel, response, filterParser)
    //.render(dataRow.get(columnName), columnName, simpleIdentifier, id, resource);
  }

  def isForm() = response.isForm()
  def isPostEntityServerResource() = resource.isInstanceOf[PostEntityServerResource[_]]
  def isPutEntityServerResource() = resource.isInstanceOf[PutEntityServerResource[_]]

  def getFormfields() = fields.values.asJava

  def getFormfieldsWrapper(): StFormFieldsWrapper = new StFormFieldsWrapper(fields.values, this.resource.getRequest(), Map())

  def isList() = response.isList()

  // java.util.List as this is used by stringtemplate
  def getLinks(): java.util.List[LinkModel] = appModel.linksFor(resource.getClass).asJava

  def getResourceSimpleName() = resource.getClass().getSimpleName()

  def addAssociatedLinks(theData: List[java.util.Map[String, Object]]): Unit = {
    if (!(getResource().isInstanceOf[ListServerResource[_]])) {
      return ;
    }
    val listServerResource = getResource().asInstanceOf[ListServerResource[_]]
    //val links = listServerResource.getLinks();
    /*val entityResourceClass = listServerResource.getAssociatedServerResources();
    if (entityResourceClass != null) {
      val sourceAsList = theData;
      for (dataRow <- sourceAsList) {
        addLinks(links, dataRow);
      }
    }*/

    val itemLinks = appModel.linksFor(resource.getClass).filter(l => l.relation == LinkRelation.ITEM).toList

    theData.foreach(dataRow => {
      val idName = appModel.entityModelFor(resource.getClass).get.name + "|id"
      val links = itemLinks.map { item =>
        val uri = item.getUri().replace("{id}", if (dataRow.get(idName) != null) dataRow.get(idName).toString() else "???")
        "<a href='" + uri + "'>" + item.title + "</a>"
      }.mkString("&nbsp;")
      dataRow.put("_links", links)
    })
  }

  //  def addLinks(links: List[io.skysail.api.links.Link], dataRow: java.util.Map[String, Object]): Unit = {
  //    val id = guessId(dataRow);
  //    if (id == null) {
  //      return ;
  //    }
  //
  //    val linkshtml = links
  //      .filter(l => id.equals(l.refId))
  //      .map(link => {
  //        val sb = new StringBuilder();
  //
  ////        if (link.getImage(MediaType.TEXT_HTML) != null) {
  ////          sb.append("<a href='").append(link.uri).append("' title='").append(link.getTitle())
  ////            .append("' alt='").append(link.alt).append("'>")
  ////           // .append("<span class='glyphicon glyphicon-").append(link.getImage(MediaType.TEXT_HTML))
  ////            .append("' aria-hidden='true'></span>").append("</a>");
  ////        } else {
  //          sb.append("<a href='").append(link.uri).append("' title='").append(link.alt).append("'>")
  //            .append(link.title).append("</a>");
  //        //}
  //        return sb.toString();
  //      })
  //      .mkString("&nbsp;&nbsp;")
  //
  //    //		dataRow.put("_links", linkshtml);
  //    //
  //    //		dataRow.put("_linksNew", links.stream().filter(l -> id.equals(l.getRefId())).map(LinkTemplateAdapter::new)
  //    //				.collect(Collectors.toList()));
  //  }

  def guessId(theObject: Any): String = {
    if (!(theObject.isInstanceOf[Map[_, _]]))
      return "";
    val entity = theObject.asInstanceOf[Map[String, Object]]

    val idKey = entity.keySet.filter(key => key.endsWith("|id")).headOption
    if (idKey.isDefined && entity.get(idKey.get) != null) {
      return entity.get(idKey.get).toString().replace("#", "");
    }

    if (entity.get(ResourceRenderingModel.ID) != null) {
      val value = entity.get(ResourceRenderingModel.ID);
      return value.toString().replace("#", "");
    } else if (entity.get("@rid") != null) {
      val str = entity.get("@rid").toString();
      return str.replace("#", "");
    } else if (entity.get("name") != null) {
      return entity.get("name").toString();
    } else {
      return "";
    }
  }

  def getCreateFormLinks(): java.util.List[LinkModel] = {
    appModel.linksFor(resource.getClass).filter(l => LinkRelation.CREATE_FORM == l.relation).toList.asJava
  }

  def getApplicationContextLinks(): java.util.List[LinkModel] = {
    val allApps = skysailApplicationService.applicationListProvider.getApplications()
    val appContextResourceClasses = allApps
      .map { app => app.associatedResourceClasses }
      .flatten
      .filter(association => association._1 == APPLICATION_CONTEXT_RESOURCE)
      .map(association => association._2)

    val allApplicationModels = allApps
      .map { app => app.getApplicationModel2() }
    //.map { appModel => appModel.linksFor(resourceClass)

    val optionalResourceModels = for (
      appModel <- allApplicationModels;
      resClass <- appContextResourceClasses;
      val z = appModel.resourceModelFor(resClass)
    ) yield z

    optionalResourceModels
      .filter { m => m.isDefined }
      .map { m => m.get }
      .map { m => m.linkModel }
      .toList
      .asJava // for string template

  }

  private def adjustKeyNames(result: java.util.ArrayList[java.util.Map[String, Object]], cls: Class[_]) = {
    val p = new java.util.ArrayList[java.util.Map[String, Object]]()
    for (row <- result.asScala) {
      if (row != null) {
        val nR = new java.util.HashMap[String, Object]();
        for (key <- row.keySet().asScala) {
          nR.put(cls.getName() + "|" + key, row.get(key));
        }
        p.add(nR);
      }
    }
    p
  }

  //  def getApplications(): StMenuItemWrapper = {
  //    return getMenu(APPLICATION_MAIN_MENU);
  //  }
  //
  //  private def getMenu(category: Category): StMenuItemWrapper = {
  //    val menuItems = MenuItemUtils.getMenuItems(menuProviders, resource, category);
  //    new StMenuItemWrapper(menuItems.stream().sorted().collect(Collectors.toList()));
  //  }

}