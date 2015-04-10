/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package zero.downtime.soa.hello;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import org.apache.camel.EndpointInject;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class HelloServiceRouteTest extends BaseEmbeddedKafkaTest {

	private static final String NAME = "John";

	@EndpointInject(uri = "mock:result")
	private MockEndpoint to;

	private Producer<String, String> producer;

	@Before
	public void before() {
		Properties props = new Properties();
		props.put("metadata.broker.list", getKafkaCluster());
		props.put("serializer.class", "kafka.serializer.StringEncoder");
		props.put("request.required.acks", "1");

		ProducerConfig config = new ProducerConfig(props);
		producer = new Producer<String, String>(config);
	}

	@After
	public void after() {
		producer.close();
	}

	@Override
	protected RouteBuilder createRouteBuilder() throws Exception {
		return new HelloServiceRoute();
	}

	@Test
	public void testSynchronousRoute() throws Exception {
		context.getRouteDefinition("HelloServiceSync").adviceWith(context, new AdviceWithRouteBuilder() {
			@Override public void configure() throws Exception {
				weaveAddLast().to(to);
			}
		});

		context.start();

		to.expectedMessageCount(1);
		to.expectedBodiesReceived("Hello " + NAME + "! \n");

		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("user", NAME);

		template.sendBodyAndHeaders("direct-vm:hello", "", headers);
		assertMockEndpointsSatisfied();
	}

	@Test
	public void kafkaMessageIsConsumedByRoute() throws Exception {
		context.getRouteDefinition("HelloServiceAsync").adviceWith(context, new AdviceWithRouteBuilder() {
			@Override public void configure() throws Exception {
				weaveAddFirst().to(to);
				//weaveAddLast().to(to);
			}
		});

		//to.expectedMessageCount(5);
		//to.expectedBodiesReceivedInAnyOrder( NAME + "-0", NAME + "-1", NAME + "-2", NAME + "-3", NAME + "-4");
		for (int k = 0; k < 5; k++) {
			String msg = NAME + "-" + k;

			KeyedMessage<String, String> data = new KeyedMessage<String, String>(getKafkaTopic(), "1", msg);
			producer.send(data);
		}
		to.assertIsSatisfied(3000);
	}
}
