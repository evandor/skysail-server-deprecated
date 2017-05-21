package io.skysail.converter.forms.helper

import io.skysail.restlet.responses.ScalaSkysailResponse
import io.skysail.restlet.queries.QueryFilterParser
import io.skysail.core.model.FieldModel

case class CellRendererHelper(fieldModel: FieldModel, response: ScalaSkysailResponse[_], parser: QueryFilterParser ) {
  
}