package io.skysail.converter.forms.helper

import io.skysail.api.responses.SkysailResponse
import io.skysail.restlet.responses.ScalaSkysailResponse
import io.skysail.restlet.queries.QueryFilterParser
import io.skysail.core.model.SkysailFieldModel2

case class CellRendererHelper(fieldModel: SkysailFieldModel2, response: ScalaSkysailResponse[_], parser: QueryFilterParser ) {
  
}