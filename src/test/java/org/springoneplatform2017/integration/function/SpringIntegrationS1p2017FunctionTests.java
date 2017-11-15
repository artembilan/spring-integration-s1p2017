package org.springoneplatform2017.integration.function;

import static org.junit.Assert.assertEquals;

import java.net.URISyntaxException;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Artem Bilan
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
		properties = "spring.main.web-application-type=servlet")
public class SpringIntegrationS1p2017FunctionTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	public void upperCase() throws URISyntaxException {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> requestEntity = new HttpEntity<>("[\"foo\", \"bar\"]", httpHeaders);
		HttpEntity<String> result = this.restTemplate.postForEntity("/uppercase", requestEntity, String.class);
		assertEquals("[\"FOO\",\"BAR\"]", result.getBody());
	}

}
