package org.springoneplatform2017.integration.kafka;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Collections;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.channel.MessageChannels;
import org.springframework.integration.dsl.channel.QueueChannelSpec;
import org.springframework.integration.dsl.context.IntegrationFlowContext;
import org.springframework.integration.dsl.context.IntegrationFlowRegistration;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.jdbc.store.JdbcChannelMessageStore;
import org.springframework.integration.jdbc.store.channel.HsqlChannelMessageStoreQueryProvider;
import org.springframework.integration.store.ChannelMessageStore;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.rule.KafkaEmbedded;
import org.springframework.messaging.Message;
import org.springframework.messaging.PollableChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.junit4.SpringRunner;

import reactor.core.publisher.Flux;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;

/**
 * @author Artem Bilan
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class SpringIntegrationS1p2017KafkaTests {

	private static final String TEST_TOPIC = "testTopic";

	@ClassRule
	public static KafkaEmbedded kafkaEmbedded = new KafkaEmbedded(1, true, 1, TEST_TOPIC);

	@Autowired
	private KafkaTemplate<?, String> template;

	@Autowired
	private KafkaProperties kafkaProperties;

	@Autowired
	private PollableChannel resultChannel;

	@Autowired
	private IntegrationFlowContext integrationFlowContext;

	@BeforeClass
	public static void setup() {
		System.setProperty("spring.kafka.bootstrap-servers", kafkaEmbedded.getBrokersAsString());
	}

	@Test
	public void testKafkaReactiveIntegration() throws InterruptedException {
		this.template.sendDefault("foo");
		this.template.sendDefault("bar");
		this.template.sendDefault("baz");
		this.template.flush();

		ReceiverOptions<String, String> receiverOptions =
				ReceiverOptions.<String, String>create(this.kafkaProperties.buildConsumerProperties())
						.subscription(Collections.singleton(TEST_TOPIC));

		Flux<Flux<ConsumerRecord<String, String>>> kafkaFlux =
				KafkaReceiver.create(receiverOptions)
						.receiveAutoAck();

		Flux<Message<?>> messageFlux = kafkaFlux
				.log()
				.map(GenericMessage::new);

		IntegrationFlow reactiveKafkaFlow =
				IntegrationFlows.from(messageFlux)
						.split()
						.channel(MessageChannels.flux())
						.log(LoggingHandler.Level.WARN)
						.<ConsumerRecord, Object>transform(ConsumerRecord::value)
						.transform(String.class, String::toUpperCase)
						.channel(this.resultChannel)
						.get();

		IntegrationFlowRegistration flowRegistration =
				this.integrationFlowContext.registration(reactiveKafkaFlow)
						.register();

		Message<?> message = this.resultChannel.receive(10000);
		assertNotNull(message);
		assertEquals("FOO", message.getPayload());

		message = this.resultChannel.receive(1000);
		assertNotNull(message);
		assertEquals("BAR", message.getPayload());

		message = this.resultChannel.receive(1000);
		assertNotNull(message);
		assertEquals("BAZ", message.getPayload());

		assertNull(this.resultChannel.receive(10));

		flowRegistration.destroy();
	}

	@SpringBootApplication
	public static class IntegrationKafkaConfig {

		@Bean
		public ChannelMessageStore channelMessageStore(DataSource dataSource) {
			JdbcChannelMessageStore jdbcChannelMessageStore = new JdbcChannelMessageStore(dataSource);
			jdbcChannelMessageStore.setChannelMessageStoreQueryProvider(new HsqlChannelMessageStoreQueryProvider());
			jdbcChannelMessageStore.setPriorityEnabled(true);
			return jdbcChannelMessageStore;
		}

		@Bean
		public QueueChannelSpec resultChannel(ChannelMessageStore channelMessageStore) {
			return MessageChannels.queue(channelMessageStore, "resultQueue");
		}

	}

}
