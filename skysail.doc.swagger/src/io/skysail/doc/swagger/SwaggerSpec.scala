package io.skysail.doc.swagger

import com.fasterxml.jackson.annotation.JsonIgnore
import io.skysail.core.app.SkysailApplication
import org.restlet.Request
import java.net.InetAddress
import java.net.UnknownHostException
import io.skysail.restlet.SkysailServerResource
import scala.annotation.meta.field
import scala.beans.BeanProperty
import java.util.Arrays
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include
import io.skysail.core.domain.ScalaEntity

@JsonInclude(Include.NON_NULL)
class SwaggerSpec(skysailApplication: SkysailApplication, request: Request) extends ScalaEntity[String] {

  @JsonIgnore
  var id: Option[String] = None
  
  @BeanProperty val swagger = "2.0";
  @BeanProperty var info: SwaggerInfo = null
  @BeanProperty var host = "localhost:2021"; // : petstore.swagger.io
  @BeanProperty var basePath: String = ""
  @BeanProperty val schemes = Arrays.asList("http");
  @BeanProperty val paths = new java.util.HashMap[String, SwaggerPath]()
  @BeanProperty val definitions = new java.util.HashMap[String, SwaggerDefinition]()
  @BeanProperty val externalDocs = new SwaggerExternalDoc("skysail documentation", "https://evandor.gitbooks.io/skysail/content/")

  @JsonIgnore
  val types = scala.collection.mutable.Set[Class[_]]()

  info = new SwaggerInfo(skysailApplication);
  host = determineHost(skysailApplication, request);
  basePath = "/" + skysailApplication.getName() + skysailApplication.apiVersion.getVersionPath();
  determinePaths(skysailApplication);
  determineDefinitions(skysailApplication);

  private def determinePaths(skysailApplication: SkysailApplication) = {
    val versionPath = skysailApplication.apiVersion.getVersionPath()
    val test = skysailApplication.routesMap
      .keys.toList
      .sortWith((p1, p2) => p1.compareTo(p2) > 0)
      .filter { path => path.startsWith(versionPath) }
      .filter { path => !path.startsWith(versionPath + "/_") }
      .filter { path => path.length() > versionPath.length() + 1 }
      .foreach { path =>
        {
          val routeBuilder = skysailApplication.routesMap.get(path)
          if (routeBuilder.isDefined) {
            val targetClass = routeBuilder.get.targetClass
            if (classOf[SkysailServerResource[_]].isAssignableFrom(targetClass)) {
              try {
                val newInstance = targetClass.newInstance().asInstanceOf[SkysailServerResource[_]]
                types += newInstance.getParameterizedType()
              } catch {
                case _: Any =>
              }
            }
            paths.put(path.substring(versionPath.length()), new SwaggerPath(routeBuilder.get));
          }
        }
      }

  }

  private def determineDefinitions(skysailApplication: SkysailApplication) = {
    types
      .toList
      .sortWith((e1, e2) => e1.getName.compareTo(e2.getName) > 0)
      .foreach { entity => definitions.put(entity.getSimpleName(), new SwaggerDefinition(entity)) }
  }

  private def determineHost(skysailApplication: SkysailApplication, request: Request): String = {
    if (!skysailApplication.getHost.isEmpty()) {
      return skysailApplication.getHost
    }
    try {
      val host = InetAddress.getByName(request.getHostRef().getHostDomain()).getHostName();
      return host + ":" + request.getHostRef().getHostPort();
    } catch {
      case e: UnknownHostException => {
        e.printStackTrace();
        return request.getHostRef().getHostDomain() + ":" + request.getHostRef().getHostPort();
      }
    }
  }

}
