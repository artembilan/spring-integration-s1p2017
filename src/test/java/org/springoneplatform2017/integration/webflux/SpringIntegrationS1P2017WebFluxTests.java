package org.springoneplatform2017.integration.webflux;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
		properties = "spring.main.web-application-type=reactive")
public class SpringIntegrationS1P2017WebFluxTests {

	@LocalServerPort
	private int port;

	@Test
	public void testWebFluxIntegration() {
		WebTestClient webTestClient =
				WebTestClient.bindToServer()
						.baseUrl("http://localhost:" + this.port)
						.build();

		Flux<String> responseBody =
				webTestClient.get().uri("/sse")
						.exchange()
						.returnResult(String.class)
						.getResponseBody();

		responseBody = responseBody.log();

		StepVerifier
				.create(responseBody)
				.expectNext("foo", "bar", "baz")
				.verifyComplete();
	}

}
