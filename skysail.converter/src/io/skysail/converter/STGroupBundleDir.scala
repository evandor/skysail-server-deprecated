package io.skysail.converter

import org.stringtemplate.v4.STGroupDir
import io.skysail.server.services.StringTemplateProvider
import org.osgi.framework.Bundle
import org.restlet.resource.Resource

object STGroupBundleDir {
	val UTF8_ENCODING = "UTF-8";
	val DELIMITER_START_CHAR = '$'
	val DELIMITER_STOP_CHAR = '$'
  
}

class STGroupBundleDir(
    bundle:Bundle, 
    resource:Resource, 
    resourcePath:String,
    templateProvider:java.util.List[StringTemplateProvider]) 
extends STGroupDir(
    bundle.getResource(resourcePath), 
    STGroupBundleDir.UTF8_ENCODING, 
    STGroupBundleDir.DELIMITER_START_CHAR, 
    STGroupBundleDir.DELIMITER_STOP_CHAR) {
  
}

//public STGroupBundleDir(Bundle bundle, @NonNull Resource resource, @NonNull String resourcePath, @NonNull List<StringTemplateProvider> templateProvider) {
//		super(bundle.getResource(resourcePath), UTF8_ENCODING, DELIMITER_START_CHAR, DELIMITER_STOP_CHAR);
//
//		log.debug("new STGroupBundleDir in bundle '{}' @path '{}'", bundle.getSymbolicName(), resourcePath);
//		
//		this.optionalResourceClassName = resource.getClass().getName();
//		this.bundleSymbolicName = bundle.getSymbolicName();
//		this.groupDirName = getGroupDirName(bundle, resourcePath); // e.g. "STGroupBundleDir: skysail.server - /templates"
//		this.templateProvider = templateProvider;
//	}
