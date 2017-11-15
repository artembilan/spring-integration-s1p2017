package org.springoneplatform2017.integration.dsl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.PollableChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Artem Bilan
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class SpringIntegrationS1p2017DslTests {

	@Autowired
	private MessageChannel input;

	@Autowired
	private PollableChannel output;

	@Test
	public void testJavaDslFlow() {
		this.input.send(new GenericMessage<Object>("a,b,c,d"));

		Message<?> message = this.output.receive(10_000);

		assertNotNull(message);

		assertEquals("[A, B, C, D]", message.getPayload().toString());
	}
}
