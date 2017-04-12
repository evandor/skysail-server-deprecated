package io.skysail.http.jetty;

import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Server Config", pid="server")
public @interface ServerConfig {

    int port() default 2015;

    String productName() default "skysail";

}
