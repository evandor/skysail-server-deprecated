package io.skysail.testsupport.authentication;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.resource.ClientResource;

import io.skysail.testsupport.ScalaApplicationClient;

public class ShiroAuthenticationStrategy2 implements AuthenticationStrategy2 {

	@Override
	public ClientResource login(ScalaApplicationClient client, String username, String password) {
      ClientResource cr = new ClientResource(client.baseUrl() + "/_logout?targetUri=/");
      cr.get();
      cr = new ClientResource(client.baseUrl() + "/ShiroUmApplication/v1/_login");
      cr.setFollowingRedirects(true);
      Form form = new Form();
      form.add("username", username);
      form.add("password", password);
      cr.post(form, MediaType.TEXT_HTML);
      String credentials = cr.getResponse().getCookieSettings().getFirstValue("Credentials");
      cr = new ClientResource(client.baseUrl() + "/");
      cr.getCookies().add("Credentials", credentials);
      cr.get(MediaType.TEXT_HTML);
      return cr;
	}

}
