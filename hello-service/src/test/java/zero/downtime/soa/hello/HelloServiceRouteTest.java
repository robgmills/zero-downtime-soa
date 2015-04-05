package zero.downtime.soa.hello;

import org.apache.camel.EndpointInject;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

/**
 * @author Rob Mills
 * @version 1.0
 * @since 1.0
 */
public class HelloServiceRouteTest extends CamelTestSupport {

	@EndpointInject(uri = "mock:result")
	protected MockEndpoint resultEndpoint;

	@Test
	public void testHelloRoute() throws Exception {
		context.getRouteDefinition("HelloService").adviceWith(context, new AdviceWithRouteBuilder() {
			@Override public void configure() throws Exception {
				weaveAddFirst().setHeader("user", constant("Rob"));
				weaveAddLast().to(resultEndpoint);
			}
		});

		context.start();

		resultEndpoint.expectedMessageCount(1);
		resultEndpoint.expectedBodiesReceived("Hello Rob! \n");

		template.sendBodyAndHeaders( "direct-vm:hello", "Rob", null );
		assertMockEndpointsSatisfied();
	}

	@Override
	protected RouteBuilder createRouteBuilder() {
		return new HelloServiceRoute();
	}
}
