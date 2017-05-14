package io.skysail.doc.swagger

import io.skysail.core.app.SkysailApplication
import scala.annotation.meta.field
import scala.beans.BeanProperty

class SwaggerInfo(skysailApplication: SkysailApplication) {
  @BeanProperty val title = skysailApplication.getName()
  @BeanProperty val description = skysailApplication.getDescription()
  @BeanProperty val version = skysailApplication.apiVersion.toString()
  @BeanProperty val contact = new SwaggerContact("skysail support", "http://www.skysail.io", "evandor@gmail.com")
  @BeanProperty val license = new SwaggerLicence("Apache License 2.0", "https://www.apache.org/licenses/LICENSE-2.0")
}