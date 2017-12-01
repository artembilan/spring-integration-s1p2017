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

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.webflux.dsl.WebFlux;

import reactor.core.publisher.Flux;

/**
 * @author Artem Bilan
 */
@SpringBootApplication
public class SpringIntegrationS1p2017WebFlux {

	@Bean
	public IntegrationFlow sseFlow() {
		return IntegrationFlows
				.from(WebFlux.inboundGateway("/sse")
						.requestMapping(m -> m.produces(MediaType.TEXT_EVENT_STREAM_VALUE)))
				.handle((p, h) -> Flux.just("foo", "bar", "baz"))
				.get();
	}

}
