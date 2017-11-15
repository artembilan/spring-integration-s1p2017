package org.springoneplatform2017.integration.function;

import java.util.function.Function;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.messaging.Message;

/**
 * @author Artem Bilan
 */
@SpringBootApplication
public class SpringIntegrationS1p2017Function {

	public static void main(String[] args) {
		SpringApplication.run(SpringIntegrationS1p2017Function.class, args);
	}

	@Bean
	public IntegrationFlow uppercaseFlow() {
		return IntegrationFlows.from(MessageFunction.class, "uppercase")
				.<String, String>transform(String::toUpperCase)
				.log(LoggingHandler.Level.WARN)
				.bridge()
				.get();
	}

	public interface MessageFunction extends Function<Message<String>, Message<String>> {

	}

}
