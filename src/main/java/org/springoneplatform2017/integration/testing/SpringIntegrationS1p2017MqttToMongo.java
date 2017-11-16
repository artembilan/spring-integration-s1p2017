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
