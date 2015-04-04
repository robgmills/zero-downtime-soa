package zero.down.time.soa.api;

import org.apache.camel.builder.RouteBuilder;
import org.eclipse.jetty.server.Server;

/**
 * @author magonzal
 * @version 1.0.0
 */
public class HelloServiceApi extends RouteBuilder {

	@Override
	public void configure() throws Exception {

		//@formatter:off
		restConfiguration().component("jetty")
			.host("localhost")
			.port(getRandomServerPort())
			.contextPath("hello-service-api/rest");

		rest("/{user}").description("Say hello to User")
			.get().route().to("direct-vm:hello");
		//@formatter:on
	}

	private int getRandomServerPort() throws Exception {
		Server server = new Server(0);
		server.start();
		return server.getConnectors()[0].getLocalPort();
	}
}

