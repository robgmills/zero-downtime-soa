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

	private static final String KAFKA_1 = "kafka:localhost:9093,localhost:9094?topic=test&zookeeperHost=localhost&zookeeperPort=2181&groupId=group1";

	@Override
	public void configure() throws Exception {
		//@formatter:off
		from("direct-vm:hello").id("HelloServiceSync")
			.log(LoggingLevel.INFO, ":::: Hello ${header.user}! ::::")
			.setBody(simple("Hello ${header.user}! \\n")).end();

		from(KAFKA_1).id("HelloServiceAsync")
			.log(LoggingLevel.INFO, ":::: Hello ${body}! ::::")
			.setBody(simple("Hello ${body}! \\n")).end();
		//@formatter:off
	}
}
