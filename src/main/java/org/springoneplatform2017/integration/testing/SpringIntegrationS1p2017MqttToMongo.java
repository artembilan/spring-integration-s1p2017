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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.integration.annotation.Router;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.mongodb.outbound.MongoDbStoringMessageHandler;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.handler.annotation.Header;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;

/**
 * @author Artem Bilan
 */
@Configuration
@EnableIntegration
public class SpringIntegrationS1p2017MqttToMongo {

	@Bean
	public MqttPahoMessageDrivenChannelAdapter mqttMessageDrivenChannelAdapter() {
		MqttPahoMessageDrivenChannelAdapter channelAdapter =
				new MqttPahoMessageDrivenChannelAdapter("tcp://localhost:1884", "myMqttClient", "messages", "events");
		channelAdapter.setOutputChannelName("routingChannel");
		return channelAdapter;
	}

	@Router(inputChannel = "routingChannel", suffix = "Channel")
	public String routeByTopic(@Header(MqttHeaders.RECEIVED_TOPIC) String topic) {
		return topic;
	}

	@Bean
	@ServiceActivator(inputChannel = "eventsChannel")
	public MessageHandler mongoDbMessageHandler1() {
		MongoDbStoringMessageHandler mongoDbMessageHandler = new MongoDbStoringMessageHandler(mongoDbFactory1());
		mongoDbMessageHandler.setCollectionNameExpression(new LiteralExpression("events"));
		return mongoDbMessageHandler;
	}

	@Bean
	@ServiceActivator(inputChannel = "messagesChannel")
	public MessageHandler mongoDbMessageHandler2() {
		MongoDbStoringMessageHandler mongoDbMessageHandler = new MongoDbStoringMessageHandler(mongoDbFactory2());
		mongoDbMessageHandler.setCollectionNameExpression(new LiteralExpression("messages"));
		return mongoDbMessageHandler;
	}

	@Bean
	public MongoClient mongoClient() {
		return new MongoClient(new ServerAddress("localhost", 27018),
				MongoClientOptions.builder()
						.serverSelectionTimeout(100)
						.build());
	}

	@Bean
	public MongoDbFactory mongoDbFactory1() {
		return new SimpleMongoDbFactory(mongoClient(), "db1");
	}

	@Bean
	public MongoDbFactory mongoDbFactory2() {
		return new SimpleMongoDbFactory(mongoClient(), "db2");
	}

}
