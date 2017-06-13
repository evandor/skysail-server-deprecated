package io.skysail.testsupport.authentication;

import org.restlet.resource.ClientResource;

import io.skysail.testsupport.ScalaApplicationClient;

public interface AuthenticationStrategy2 {

	ClientResource login(ScalaApplicationClient client, String username, String password);

}
