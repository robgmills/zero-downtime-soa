package zero.downtime.soa.api;

import org.apache.camel.builder.RouteBuilder;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Rest DSL using the jetty component to expose the Hello Service API
 *
 * @author Mariano Gonzalez
 * @version 1.0
 * @since 1.0
 */
public class HelloServiceApi extends RouteBuilder {

	private static String KAFKA = "kafka:{{helloApi.kafkaCluster}}?topic={{helloApi.kafkaTopic}}&serializerClass={{helloApi.kafkaSerializer}}";

	@Override public void configure() throws Exception {
		//@formatter:off
		restConfiguration().component("jetty")
			.host("localhost")
			.port(getRandomServerPort());

		rest("/rest-api")
			.get("/sync/{user}").to("direct-vm:hello")
			.get("/async/{user}").route().transform(header("user"))
			.to(KAFKA)
			.setBody(simple("Thanks ${header.user} your messages is being processed. \\n"));
		//@formatter:on
	}

	/**
	 * Looks up an available port number on the host.
	 *
	 * @return a port number available for use
	 * @throws IOException if unable to find an available ServerSocket
	 */
	protected int getRandomServerPort() throws IOException {
		return (new ServerSocket(0)).getLocalPort();
	}
}

