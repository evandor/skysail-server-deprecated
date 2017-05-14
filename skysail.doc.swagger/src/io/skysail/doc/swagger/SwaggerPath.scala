package io.skysail.doc.swagger

import io.skysail.api.doc.ApiMetadata
import io.skysail.restlet.{ RouteBuilder, SkysailServerResource }
import org.slf4j.LoggerFactory
import scala.annotation.meta.field
import scala.beans.BeanProperty
import io.skysail.restlet.resources.EntityServerResource
import io.skysail.restlet.resources.PostEntityServerResource
import io.skysail.restlet.resources.PutEntityServerResource
import java.util.Arrays
import scala.collection.JavaConverters._
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import org.restlet.resource.ServerResource

object SwaggerPath {
  val PARAMETERS = "parameters"
  val RESPONSES = "responses"
  val PRODUCES = "produces"
  val DESCRIPTION = "description"
  val SUMMARY = "summary"
  val TAGS = "tags"
}

@JsonInclude(Include.NON_NULL)
class SwaggerPath(routeBuilder: RouteBuilder) {

  private val log = LoggerFactory.getLogger(this.getClass())

  @BeanProperty var get: java.util.Map[String, Object] = _
  @BeanProperty var post: java.util.Map[String, Object] = _
  @BeanProperty var put: java.util.Map[String, Object] = _
  @BeanProperty var delete: java.util.Map[String, Object] = _

  val parentClass = routeBuilder.targetClass.getSuperclass()

  private var apiMetadata = ApiMetadata.builder().build();

  try {
    val newInstance = routeBuilder.targetClass.newInstance().asInstanceOf[SkysailServerResource[_]]
    apiMetadata = newInstance.getApiMetadata();
  } catch {
    case e: Any => log.error(e.getMessage(), e);
  }

  if (classOf[EntityServerResource[_]].isAssignableFrom(parentClass)) {
    get = initIfNeccessary(get);
    addApiMetadataForGet(get, apiMetadata);
    get.put(SwaggerPath.PRODUCES, Arrays.asList("application/json"))
    if (routeBuilder.pathVariables.size > 0) {
      val parameterList = new java.util.ArrayList[SwaggerParameter]()
      routeBuilder.pathVariables.foreach(path => parameterList.add(SwaggerParameter(path)))
      get.put(SwaggerPath.PARAMETERS,parameterList)
    }
    get.put(SwaggerPath.RESPONSES, addGetResponses(routeBuilder))
  } else if (classOf[PostEntityServerResource[_]].isAssignableFrom(parentClass)) {
    get = initIfNeccessary(get);
    addApiMetadataForGet(get, apiMetadata);
    post = initIfNeccessary(post);
    addApiMetadataForPost(post, apiMetadata);
    get.put(SwaggerPath.PRODUCES, Arrays.asList("application/json"))
    // get.put(SwaggerPath.PARAMETERS, new SwaggerParameter(routeBuilder));
    get.put(SwaggerPath.RESPONSES, addGetResponses(routeBuilder))

    post.put(SwaggerPath.DESCRIPTION, "default swagger post path description")
    post.put(SwaggerPath.PRODUCES, Arrays.asList("application/json"))
    post.put(SwaggerPath.RESPONSES, addPostResponse(routeBuilder))

  } else if (classOf[PutEntityServerResource[_]] isAssignableFrom (parentClass)) {
    get = initIfNeccessary(get);
    put = initIfNeccessary(put);
    post = initIfNeccessary(post);

    addApiMetadataForGet(get, apiMetadata);
    addApiMetadataForPut(put, apiMetadata);
    get.put(SwaggerPath.PRODUCES, Arrays.asList("application/json"))
    // get.put(SwaggerPath.PARAMETERS, new SwaggerParameter(routeBuilder));
    get.put(SwaggerPath.RESPONSES, addGetResponses(routeBuilder))
    post.put(SwaggerPath.PRODUCES, List("application/json"))
    post.put(SwaggerPath.RESPONSES, addPostResponse(routeBuilder))
  }

  private def initIfNeccessary(theMap: java.util.Map[String, Object]) = {
    if (theMap == null) new java.util.HashMap[String, Object]() else theMap
  }

  def addGetResponses(routeBuilder: RouteBuilder) = {
    val responseMap = new java.util.HashMap[String, Object]()
    responseMap.put("200", Map[String, String](SwaggerPath.DESCRIPTION -> "post entity get").asJava)
    responseMap
  }

  private def addPostResponse(routeBuilder: RouteBuilder) = {
    post.put("summary", ("POST endpoint defined in " + routeBuilder.targetClass.getSimpleName()))
    analyse(routeBuilder.targetClass);
    post.put(SwaggerPath.PRODUCES, Arrays.asList("application/json"))
    val responseMap = new java.util.HashMap[String, Object]()
    responseMap.put("200", Map[String, String](SwaggerPath.DESCRIPTION -> "post entity get").asJava)
    responseMap
  }

  private def addApiMetadataForGet(map: java.util.Map[String, Object], apiMetadata: ApiMetadata) = {
    map.put(SwaggerPath.SUMMARY, apiMetadata.getSummaryForGet())
    map.put(SwaggerPath.DESCRIPTION, apiMetadata.getDescriptionForGet())
    map.put(SwaggerPath.TAGS, apiMetadata.getTagsForGet())
  }

  private def addApiMetadataForPost(map: java.util.Map[String, Object], apiMetadata: ApiMetadata) = {
    map.put(SwaggerPath.SUMMARY, apiMetadata.getSummaryForPost())
    map.put(SwaggerPath.DESCRIPTION, apiMetadata.getDescriptionForPost())
    map.put(SwaggerPath.TAGS, apiMetadata.getTagsForPost())
  }

  private def addApiMetadataForPut(map: java.util.Map[String, Object], apiMetadata: ApiMetadata) = {
    map.put(SwaggerPath.SUMMARY, apiMetadata.getSummaryForPut())
    map.put(SwaggerPath.DESCRIPTION, apiMetadata.getDescriptionForPut())
    map.put(SwaggerPath.TAGS, apiMetadata.getTagsForPut())
  }

  private def addApiMetadataForDelete(map: java.util.Map[String, Object], apiMetadata: ApiMetadata) = {
    map.put(SwaggerPath.SUMMARY, apiMetadata.getSummaryForDelete())
    map.put(SwaggerPath.DESCRIPTION, apiMetadata.getDescriptionForDelete())
    map.put(SwaggerPath.TAGS, apiMetadata.getTagsForDelete())
  }

  private def analyse(targetClass: Class[_ <: ServerResource]) = {
    try {
      val newInstance = targetClass.newInstance();
      val getDescriptionMethod = newInstance.getClass().getMethod("getDescription");
      val desc = getDescriptionMethod.invoke(newInstance).asInstanceOf[String]
      post.put(SwaggerPath.DESCRIPTION, if (desc != null) desc else "")
    } catch {
      case e: Any => log.error(e.getMessage(), e)
    }

  }

}
