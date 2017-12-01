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

package org.springoneplatform2017.integration.testing;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.springframework.integration.test.matcher.HeaderMatcher.hasHeader;
import static org.springframework.integration.test.matcher.PayloadMatcher.hasPayload;

import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.integration.test.context.MockIntegrationContext;
import org.springframework.integration.test.context.SpringIntegrationTest;
import org.springframework.integration.test.mock.MockIntegration;
import org.springframework.integration.test.mock.MockMessageHandler;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Artem Bilan
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = SpringIntegrationS1p2017MqttToMongo.class)
@SpringIntegrationTest(noAutoStartup = "mqttMessageDrivenChannelAdapter")
@DirtiesContext
public class SpringIntegrationS1p2017TestingTests {

	@Autowired
	private MessageChannel routingChannel;

	@Autowired
	private MockIntegrationContext mockIntegrationContext;

	@Test
	public void testIntegrationScenario() throws InterruptedException {
		MessageSource<?> mockMessageSource =
				MockIntegration.mockMessageSource(
						new GenericMessage<>("{\"event\": \"myEvent\"}", Collections.singletonMap(MqttHeaders.RECEIVED_TOPIC, "events")),
						new GenericMessage<>("{\"message\": \"myMessage\"}", Collections.singletonMap(MqttHeaders.RECEIVED_TOPIC, "messages")),
						new GenericMessage<>("{\"event\": \"myEvent2\"}", Collections.singletonMap(MqttHeaders.RECEIVED_TOPIC, "events")));

		MockMessageHandler mockEventsMessageHandler =
				MockIntegration.mockMessageHandler()
						.handleNext(message -> {
							assertThat(message, hasPayload(containsString("myEvent")));
							assertThat(message, hasHeader(MqttHeaders.RECEIVED_TOPIC, "events"));
						})
						.handleNext(message -> {
							assertThat(message, hasPayload(containsString("myEvent2")));
							assertThat(message, hasHeader(MqttHeaders.RECEIVED_TOPIC, "events"));
						});

		this.mockIntegrationContext
				.substituteMessageHandlerFor("springIntegrationS1p2017MqttToMongo.mongoDbMessageHandler1.serviceActivator",
						mockEventsMessageHandler);

		this.routingChannel.send(mockMessageSource.receive());

		MockMessageHandler mockMessagesMessageHandler =
				MockIntegration.mockMessageHandler()
						.handleNext(message -> {
							assertThat(message, hasPayload(containsString("myMessage")));
							assertThat(message, hasHeader(MqttHeaders.RECEIVED_TOPIC, "messages"));
						});

		this.mockIntegrationContext
				.substituteMessageHandlerFor("springIntegrationS1p2017MqttToMongo.mongoDbMessageHandler2.serviceActivator",
						mockMessagesMessageHandler);

		this.routingChannel.send(mockMessageSource.receive());

		this.routingChannel.send(mockMessageSource.receive());
		this.routingChannel.send(mockMessageSource.receive());
	}

}
