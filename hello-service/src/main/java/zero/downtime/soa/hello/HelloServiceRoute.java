package zero.downtime.soa.hello;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;

/**
 * A simple OSGi micro service route that echoes a name in a
 * welcome message.
 *
 * @author Rob Mills
 * @version 1.0
 * @since 1.0
 */
public class HelloServiceRoute extends RouteBuilder {

	static final String KAFKA = "kafka:{{hello.kafkaCluster}}?topic={{hello.kafkaTopic}}&groupId={{hello.kafkaGroup}}&zookeeperHost={{hello.zookeeperHost}}&zookeeperPort={{hello.zookeeperPort}}";

	@Override
	public void configure() throws Exception {
		//@formatter:off
		from("direct-vm:hello")
				.id("HelloServiceSync")
			.log(LoggingLevel.INFO, ":::: Hello ${header.user}! ::::")
			.setBody(simple("Hello ${header.user}! \\n"))
		.end();

		from(KAFKA)
				.id("HelloServiceAsync")
			.log(LoggingLevel.INFO, ":::: Hello ${body}! ::::")
			.setBody(simple("Hello ${body}! \\n"))
		.end();
		//@formatter:off
	}
}
