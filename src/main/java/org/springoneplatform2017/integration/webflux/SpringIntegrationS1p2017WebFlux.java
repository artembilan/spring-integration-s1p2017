package org.springoneplatform2017.integration.webflux;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.webflux.dsl.WebFlux;

import reactor.core.publisher.Flux;

/**
 * @author Artem Bilan
 */
@SpringBootApplication
public class SpringIntegrationS1p2017WebFlux {

	public static void main(String[] args) {
		SpringApplication springApplication = new SpringApplication(SpringIntegrationS1p2017WebFlux.class);
		springApplication.setWebApplicationType(WebApplicationType.REACTIVE);
		springApplication.run(args);
	}

	@Bean
	public IntegrationFlow sseFlow() {
		return IntegrationFlows
				.from(WebFlux.inboundGateway("/sse")
						.requestMapping(m -> m.produces(MediaType.TEXT_EVENT_STREAM_VALUE)))
				.handle((p, h) -> Flux.just("foo", "bar", "baz"))
				.log(LoggingHandler.Level.WARN)
				.bridge()
				.get();
	}

}
