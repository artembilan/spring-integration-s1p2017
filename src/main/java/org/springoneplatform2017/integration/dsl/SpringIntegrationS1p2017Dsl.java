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
				.<String, String>transform(String::toUpperCase)
				.aggregate()
				.channel("output")
				.get();
	}

	@Bean
	public PollableChannel output() {
		return new QueueChannel();
	}

}
