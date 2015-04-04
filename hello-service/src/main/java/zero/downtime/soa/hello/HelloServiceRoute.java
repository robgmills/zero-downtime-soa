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

	@Override
	public void configure() throws Exception {
		from("direct-vm:hello").id("HelloService")
				.log(LoggingLevel.INFO, ":::: Hello ${header.user}! ::::")
				.setBody( simple("Hello ${header.user}!") )
		.end();
	}

}
