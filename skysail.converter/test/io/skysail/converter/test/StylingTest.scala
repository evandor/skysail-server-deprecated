package io.skysail.converter.test

import org.mockito.Mockito
import io.skysail.restlet.ScalaSkysailServerResource
import org.restlet.Request
import org.restlet.Response
import org.restlet.data.Reference
import org.junit.Test
import io.skysail.converter.Styling

import org.junit.Assert._
import org.mockito.Mockito._
import org.hamcrest.CoreMatchers._
import org.restlet.data.Form
import org.restlet.util.Series
import org.junit.runner.RunWith
import org.junit.Ignore

class StylingTest {
  
  val resource = mock(classOf[ScalaSkysailServerResource]);

  val request = mock(classOf[Request]);
	val response = mock(classOf[Response]);
	val resourceRef = mock(classOf[Reference]);

	@Test(expected = classOf[NullPointerException])
	@Ignore
	def styling_from_null_request_throws_exceptiont():Unit = Styling.determineFrom(null)
	
	@Test
	def styling_from_empty_request_returns_default():Unit = assertThat(Styling.determineFrom(resource).getName(), is(""))
	
	@Test
	@Ignore
	def styling_is_derived_from_parameter_and_saved_to_cookie() {
		val query = new Form();
		query.add("_styling","semanticui");
		
		val cookieSettings = mock(classOf[Series[_]]);

		when(resource.getQuery()).thenReturn(query);
		when(resource.getRequest()).thenReturn(request);
		when(resource.getResponse()).thenReturn(response);
		//when(response.getCookieSettings()).thenReturn(cookieSettings);
		when(request.getResourceRef()).thenReturn(resourceRef);
		when(resourceRef.getPath()).thenReturn("thePath");

		val style = Styling.determineFrom(resource);
		
		assertThat(style.getName(), is("semanticui"));
		verify(cookieSettings,times(1)).add(org.mockito.Matchers.any());
	}
}