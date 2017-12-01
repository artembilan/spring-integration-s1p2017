/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
