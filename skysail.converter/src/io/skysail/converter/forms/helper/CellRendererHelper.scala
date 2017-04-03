package io.skysail.converter.forms.helper

import io.skysail.restlet.model.ScalaSkysailFieldModel
import io.skysail.api.responses.SkysailResponse
import io.skysail.restlet.responses.ScalaSkysailResponse
import io.skysail.restlet.queries.QueryFilterParser

case class CellRendererHelper(fieldModel: ScalaSkysailFieldModel, response: ScalaSkysailResponse[_], parser: QueryFilterParser ) {
  
}