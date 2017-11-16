package org.springoneplatform2017.integration.dsl;

		import org.springframework.context.annotation.Bean;
		import org.springframework.context.annotation.Configuration;
		import org.springframework.integration.channel.QueueChannel;
		import org.springframework.integration.config.EnableIntegration;
		import org.springframework.integration.dsl.IntegrationFlow;
		import org.springframework.integration.dsl.IntegrationFlows;
		import org.springframework.messaging.PollableChannel;

/**
 * @author Artem Bilan
 */
@Configuration
@EnableIntegration
public class SpringIntegrationS1p2017Dsl {

	@Bean
	public IntegrationFlow splitAggregateFlow() {
		return IntegrationFlows
				.from("input")
				.split(splitter -> splitter.delimiters(","))
//				.channel(c -> c.executor(Executors.newCachedThreadPool()))
				.<String, String>transform(String::toUpperCase)
//				.resequence()
				.aggregate()
				.channel("output")
				.get();
	}

	@Bean
	public PollableChannel output() {
		return new QueueChannel();
	}

}
