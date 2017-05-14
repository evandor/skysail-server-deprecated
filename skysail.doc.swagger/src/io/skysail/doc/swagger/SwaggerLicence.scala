package io.skysail.doc.swagger

import scala.annotation.meta.field
import scala.beans.BeanProperty

class SwaggerLicence(
    @BeanProperty val name: String,
    @BeanProperty val url: String
)