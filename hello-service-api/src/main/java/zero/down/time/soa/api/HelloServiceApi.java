package zero.down.time.soa.api;

import org.apache.camel.builder.RouteBuilder;

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
			.port(8080)
			.contextPath("hello-service-api/rest");

		rest("/{user}").description("Say hello to User")
			.get().route().to("direct-vm:hello");
		//@formatter:on
	}
}

