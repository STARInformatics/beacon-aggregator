package bio.knowledge.aggregator.tests;


import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;


public class AggregatorTests {
	@Test
	public void givenExistingCURIEtoBiolink_then200IsReceived()
	  throws ClientProtocolException, IOException {
	 
	   // Given
	   HttpUriRequest request = new HttpGet( "https://biolink-kb.ncats.io/concepts/MESH:D016640");

	   // When
	   CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute( request );

	   // Then
	   assertThat(
	     httpResponse.getStatusLine().getStatusCode(),
	     equalTo(HttpStatus.OK));
	}
	
	@Test
	public void 
	givenRequestWithNoAcceptHeader_whenRequestIsExecuted_thenDefaultResponseContentTypeIsJson()
	  throws ClientProtocolException, IOException {
	 
	   // Given
	   String jsonMimeType = "application/json";
	   HttpUriRequest request = new HttpGet( "https://biolink-kb.ncats.io/concepts/MESH:D016640" );

	   // When
	   CloseableHttpResponse response = HttpClientBuilder.create().build().execute( request );

	   // Then
	   String mimeType = ContentType.getOrDefault(response.getEntity()).getMimeType();
	   assertEquals( jsonMimeType, mimeType );
	   
	   
	}

}
