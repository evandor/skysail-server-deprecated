package io.skysail.testsupport

import org.restlet.data.MediaType
import org.slf4j.LoggerFactory
import io.skysail.testsupport.authentication.HttpBasicAuthenticationStrategy2
import ScalaApplicationClient.{TESTTAG => logPrefix} 

object ScalaApplicationBrowser {
  val HOST = "http://localhost"
}

class ScalaApplicationBrowser(val appName: String, port: Integer) {

  private val log = LoggerFactory.getLogger(this.getClass())

  //  @Getter
  //    protected ApplicationBrowser2 parentEntityBrowser;
  //
  //    protected SecureRandom random = new SecureRandom();
  //
  //    @Getter
  //    @Setter
  var id: String = ""
  def setId(theId: String) = this.id = theId
  //
  //    protected static final String ;
  //
  //    protected MediaType mediaType;
  var client: ScalaApplicationClient = _
  //
  //    private String defaultUser = null;
  //    private Integer port = 2014;
  //
  //    @Getter
  //    private String url;

  //
  //    @Getter
  val authenticationStrategy = new HttpBasicAuthenticationStrategy2();

  val url = ScalaApplicationBrowser.HOST + ":" + port
  log.info(s"$logPrefix creating new browser client with url '$url' for Application '$appName' and mediaType 'MediaType.TEXT_HTML'")
  client = new ScalaApplicationClient(url, appName)

  def this(url: String) = this(url,2014)
  //
  //    abstract protected Form createForm(String entity);
  //
  //    public void setPort(String port) {
  //        this.port = Integer.parseInt(port);
  //    }
  //
  //    protected String getBaseUrl() {
  //        return HOST + (port != null ? ":" + port : "");
  //    }
  //
  //    public ApplicationBrowser2 login() {
  //        return loginAs(defaultUser, "skysail");
  //    }
  //
  def loginAs(username: String, password: String): ScalaApplicationBrowser = {
    log.info(s"$ScalaApplicationBrowser.TESTTAG logging in as user '$username'")
    client.loginAs(authenticationStrategy, username, password)
    return this
  }

  //
  //    @SuppressWarnings("unchecked")
  //    public ApplicationBrowser2 asUser(String username) {
  //        this.defaultUser = username;
  //        login();
  //        return this;
  //    }
  //
  //    public void setUser(String defaultUser) {
  //        this.defaultUser = defaultUser;
  //        if (parentEntityBrowser != null) {
  //            this.parentEntityBrowser.setUser(defaultUser);
  //        }
  //    }
  //
  //    public Status getStatus() {
  //        return client.getResponse().getStatus();
  //    }
  //
  //    protected void findAndDelete(String id) {
  //        client.gotoAppRoot().followLinkTitleAndRefId("update", id).followLink(Method.DELETE);
  //    }

}