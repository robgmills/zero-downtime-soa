package zero.downtime.soa.api;

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

	/**
	 * Looks up an available port number on the host
	 * by creating a dummy Jetty Server.
	 *
	 * TODO: there's probably a better way to do this
	 *
	 * @return a port number available for use
	 * @throws Exception
	 */
	protected int getRandomServerPort() throws Exception {
		Server server = new Server(0);
		server.start();
		int port = server.getConnectors()[0].getLocalPort();
		server.stop();
		return port;
	}
}

