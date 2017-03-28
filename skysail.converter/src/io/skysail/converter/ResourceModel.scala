package io.skysail.converter

import io.skysail.restlet.ScalaSkysailServerResource
import io.skysail.api.responses.SkysailResponse
import io.skysail.api.um.UserManagementProvider
import io.skysail.server.rendering.Theme
import org.restlet.representation.Variant

class ResourceModel(resource: ScalaSkysailServerResource, skysailResponse: SkysailResponse[_],  userManagementProvider: UserManagementProvider,
			 target: Variant, theming: Theming) {
  
}