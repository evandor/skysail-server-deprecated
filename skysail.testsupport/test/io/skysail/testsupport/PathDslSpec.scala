package io.skysail.testsupport

import io.skysail.testsupport.PathDsl._
import org.scalatest._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.slf4j.LoggerFactory
import org.mockito._
import org.mockito.Mockito.{ mock, when }
import org.mockito.MockitoAnnotations
import org.restlet.security.Authenticator
import org.osgi.service.component.ComponentContext
import org.restlet.Context
import org.restlet.Request
import org.restlet.Response
import org.junit._
import org.assertj.core.api.Assertions._


class PathDslSpec extends FlatSpec /*with BeforeAndAfterEach*/ {

  "A " should "contai" in {
    val path = "a" --> "b"
    assertThat(path.elems.size).isEqualTo(2)
  }
}