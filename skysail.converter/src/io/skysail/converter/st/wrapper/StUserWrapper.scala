package io.skysail.converter.st.wrapper

import io.skysail.restlet.SkysailServerResource
import io.skysail.api.um.UserManagementProvider

object StUserWrapper {
  val DEMO = "demo";
  val ANONYMOUS = "anonymous";
}

class StUserWrapper(userManagementProvider: UserManagementProvider, resource: SkysailServerResource, peerName: String) {

  def getPrincipal() = {
    val principal = userManagementProvider.getAuthenticationService().getPrincipal(resource.getRequest());
    if (principal.getName().equals(StUserWrapper.ANONYMOUS)) null else principal
  }

  def getUsername() = if (getPrincipal() == null) StUserWrapper.ANONYMOUS else getPrincipal().getName()

  def isDeveloper() = {
    val clientInfo = this.resource.getRequest().getClientInfo();
    //this.userManagementProvider.getAuthorizationService().getEnroler().enrole(clientInfo);
    true; //subject.hasRole("developer");
  }

  def isAdmin() =  true //subject.hasRole("admin");

  def isDemoUser() = getUsername().equals(StUserWrapper.DEMO)
  
  override def toString() = s"username: $getUsername: isAdmin: $isAdmin, isDeveloper: $isDeveloper"

//  public String getBackend() {
//    if (peerName == null || peerName.trim().length() == 0) {
//      return "";
//    }
//    return "[" + peerName + "] ";
//  }

//  public List < String > getPeers () {
//    return Collections.emptyList();
//  }
}