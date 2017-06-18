package io.skysail.converter.forms.helper

import io.skysail.core.restlet.responses.ScalaSkysailResponse
import io.skysail.core.restlet.queries.QueryFilterParser
import io.skysail.core.model.FieldModel

case class CellRendererHelper(fieldModel: FieldModel, response: ScalaSkysailResponse[_], parser: QueryFilterParser ) {
  
}