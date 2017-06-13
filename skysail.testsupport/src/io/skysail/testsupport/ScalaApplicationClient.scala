package io.skysail.testsupport

import org.restlet.data.MediaType
import org.slf4j.LoggerFactory
import org.restlet.resource.ClientResource
import org.restlet.data.ChallengeResponse
import org.restlet.representation.Representation
import org.restlet.data.Method
import io.skysail.core.model.LinkModel
import java.net.URI
import ScalaApplicationClient.{ TESTTAG => logPrefix }
import io.skysail.testsupport.PathDsl._
import org.restlet.data.Form
import io.skysail.testsupport.authentication.AuthenticationStrategy2

object ScalaApplicationClient {
  val TESTTAG = " > TEST:";
}

class ScalaApplicationClient(val baseUrl: String, appName: String) {

  private val log = LoggerFactory.getLogger(this.getClass())

  var credentials = ""
  var url = ""
  var cr: ClientResource = _
  var currentRepresentation: Representation = _
  var challengeResponse: ChallengeResponse = _

  //  def setUrl(url: String): ScalaApplicationClient = {
  //    log.info(s"$ScalaApplicationClient.TESTTAG setting browser client url to '$url'");
  //    this.url = url;
  //    return this
  //  }

  def get(path: RouteDef[_], mediaType: MediaType) = {
    path.elems.foreach { pathElement => followPathElement(Method.GET, pathElement, mediaType) }
    path.url = url
    currentRepresentation
  }

  def post(form: Form, path: RouteDef[_], acceptMediaType: MediaType) = {
    path.elems.init.foreach { pathElement => followPathElement(Method.GET, pathElement, acceptMediaType) }
    followPathElement(Method.POST, path.elems.last, acceptMediaType, form, MediaType.APPLICATION_WWW_FORM)
    path.url = url
    currentRepresentation
  }

  private def followPathElement(m: Method, p: PathElem, t: MediaType, e: AnyRef = null, c: MediaType = null) = {
    p.name match {
      case "/" => gotoRoot()
      case p => followLinkTitle(m, p, e, t,c)
    }
  }

  def get(mediaType: MediaType = MediaType.APPLICATION_JSON): Representation = {
    val currentUrl = baseUrl + url;
    log.info(s"$logPrefix issuing GET on '$currentUrl', providing credentials '$credentials'")
    cr = new ClientResource(currentUrl);
    //cr.setFollowingRedirects(false);
    if (credentials != null && credentials.trim().size > 0) {
      cr.getCookies().add("Credentials", credentials);
    }
    cr.setChallengeResponse(challengeResponse);
    return cr.get(mediaType);
  }

  def gotoRoot(): ScalaApplicationClient = {
    log.info(s"$logPrefix resetting path to root URL '/'")
    url = "/";
    get();
    this
  }

  def gotoAppRoot(mediaType: MediaType = MediaType.APPLICATION_JSON) = {
    gotoRoot().followLinkTitle(Method.GET, appName, "", mediaType, null);
    this
  }

  def post(entity: AnyRef, mediaType: MediaType): Representation = {
    log.info(s"$logPrefix issuing POST on '$url', providing credentials $credentials");
    //url = if (url.contains("?")) url + "&" else url + "?") + "xxx";//SkysailServerResource.NO_REDIRECTS ;
    cr = new ClientResource(url + "?media=json");
    cr.setFollowingRedirects(false);
    cr.getCookies().add("Credentials", credentials);
    cr.setChallengeResponse(challengeResponse);
    return cr.post(entity, mediaType);
  }

  def loginAs(authenticationStrategy: AuthenticationStrategy2, username: String, password: String): ScalaApplicationClient = {
    cr = authenticationStrategy.login(this, username, password);
    challengeResponse = cr.getChallengeResponse();
    credentials = cr.getCookies().getFirstValue("Credentials");
    return this;
  }

  def followLinkTitle(m: Method, linkTitle: String, e: AnyRef, mediaType: MediaType = MediaType.APPLICATION_JSON, contentMediaType: MediaType = null): ScalaApplicationClient = {
    follow(new ScalaLinkTitlePredicate(linkTitle, cr.getResponse().getHeaders()), m, e, mediaType)
  }

  private def follow(predicate: ScalaLinkPredicate, method: Method, entity: AnyRef, acceptMediaType: MediaType, contentMediaType: MediaType = null) = {
    val currentHeader = cr.getResponse().getHeaders();
    val linkheader = currentHeader.getFirstValue("Link");
    if (linkheader == null) {
      throw new IllegalStateException("no link header found");
    }
    val links = linkheader.split(",").map { l => LinkModel.fromLinkheader(l) }.toList
    val theLink = getTheOnlyLink(predicate, links);

    var isAbsolute = false;
    try {
      val url2 = new URI(theLink.getUri());
      isAbsolute = url2.isAbsolute();
    } catch {
      case _: Any =>
    }

    url = if (isAbsolute) theLink.getUri() else baseUrl + theLink.getUri()
    cr = new ClientResource(url);
    cr.getCookies().add("Credentials", credentials);
    cr.setChallengeResponse(challengeResponse);

    if (method != null) {
      // TODO
      //            if (!(theLink.getVerbs().contains(method))) {
      //                throw new IllegalStateException("method " + method + " not eligible for link " + theLink);
      //            }
      if (Method.GET.equals(method)) {
        log.info(s"$logPrefix issuing GET on '$url', providing credentials '$credentials'");
        currentRepresentation = cr.get(acceptMediaType);
      } else if (Method.DELETE.equals(method)) {
        log.info(s"$logPrefix issuing DELETE on '$url', providing credentials '$credentials'");
        currentRepresentation = cr.delete(acceptMediaType);
      } else if (Method.POST.equals(method)) {
        log.info(s"$logPrefix issuing POST on '$url', acceptedMediaType: '$acceptMediaType', entity: '$entity', credentials: '$credentials'");
        currentRepresentation = cr.post(entity, acceptMediaType);
      } else if (Method.PUT.equals(method)) {
        log.info(s"$logPrefix issuing PUT on '$url' with entity '$entity', providing credentials '$credentials'");
        currentRepresentation = cr.put(entity, acceptMediaType);
      } else {
        throw new UnsupportedOperationException();
      }
    } else {
      log.info(s"$logPrefix issuing GET on '$url', providing credentials $credentials");
      currentRepresentation = cr.get(acceptMediaType);
      //url = currentRepresentation.getLocationRef().toUri().toString();
    }
    this;
  }

  //private def follow(m: Method, predicate: ScalaLinkPredicate, mediaType: MediaType): ScalaApplicationClient = follow(predicate, m, null, mediaType)

  private def getTheOnlyLink(predicate: ScalaLinkPredicate, links: List[LinkModel]): LinkModel = {
    val filteredLinks = links.filter(l => predicate.apply(l)).toList
    if (filteredLinks.size == 0 && predicate.isInstanceOf[ScalaLinkTitlePredicate] && !(predicate.isInstanceOf[ScalaLinkSubTitlePredicate])) {
      log.info(s"$logPrefix didn't find exact link, trying substring");
      val t = predicate.asInstanceOf[ScalaLinkTitlePredicate]
      return getTheOnlyLink(new ScalaLinkSubTitlePredicate(t.title, t.series), links);
    }
    if (filteredLinks.size == 0) {
      throw new IllegalStateException("could not find link for predicate " + predicate);
    }
    if (filteredLinks.size > 1) {
      throw new IllegalStateException("too many candidates found for predicate " + predicate);
    }
    filteredLinks.head
  }

  def getLocation() = cr.getLocationRef()

  //    public void setUrlFromCurrentRepresentation() {
  //        url = currentRepresentation.getLocationRef().toUri().toString();
  //    }

}