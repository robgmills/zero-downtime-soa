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

import org.apache.camel.CamelContext;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.test.AvailablePortFinder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import zero.downtime.soa.hello.embedded.EmbeddedKafkaCluster;
import zero.downtime.soa.hello.embedded.EmbeddedZookeeper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class BaseEmbeddedKafkaTest extends CamelTestSupport {

	public static final String TOPIC = "test";
	public static final String HOST = "localhost";

	static EmbeddedZookeeper embeddedZookeeper;
	static EmbeddedKafkaCluster embeddedKafkaCluster;

	private static volatile int zookeeperPort;

	private static volatile int kafkaPort;

	private static volatile String kafkaCluster;

	@BeforeClass
	public static void beforeClass() {
		// start from somewhere in the 23xxx range
		zookeeperPort = AvailablePortFinder.getNextAvailable(23000);
		// find another ports for proxy route test
		kafkaPort = AvailablePortFinder.getNextAvailable(24000);

		embeddedZookeeper = new EmbeddedZookeeper(zookeeperPort);
		List<Integer> kafkaPorts = new ArrayList<Integer>();
		// -1 for any available port
		kafkaPorts.add(kafkaPort);
		embeddedKafkaCluster = new EmbeddedKafkaCluster(embeddedZookeeper.getConnection(), new Properties(), kafkaPorts);
		kafkaCluster = HOST + ":" + kafkaPort;
		try {
			embeddedZookeeper.startup();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("### Embedded Zookeeper connection: " + embeddedZookeeper.getConnection());
		embeddedKafkaCluster.startup();
		System.out.println("### Embedded Kafka cluster broker list: " + embeddedKafkaCluster.getBrokerList());
	}

	@AfterClass
	public static void afterClass() {
		embeddedKafkaCluster.shutdown();
		embeddedZookeeper.shutdown();
	}

	@Override
	protected JndiRegistry createRegistry() throws Exception {
		JndiRegistry jndi = super.createRegistry();

		Properties prop = new Properties();
		prop.setProperty("hello.zookeeperPort", "" + getZookeeperPort());
		prop.setProperty("hello.zookeeperHost", getZookeeperHost());
		prop.setProperty("hello.kafkaCluster", getKafkaCluster());
		prop.setProperty("hello.kafkaTopic", getKafkaTopic());
		prop.setProperty("hello.kafkaGroup", "testGroup");

		jndi.bind("prop", prop);
		return jndi;
	}

	@Override
	protected CamelContext createCamelContext() throws Exception {
		CamelContext context = super.createCamelContext();
		PropertiesComponent propertiesComponent = new PropertiesComponent("ref:prop");
		context.addComponent("properties", propertiesComponent);
		return context;
	}

	protected static String getZookeeperHost() {
		return HOST;
	}

	protected static int getZookeeperPort() {
		return zookeeperPort;
	}

	protected static String getKafkaCluster() {
		return kafkaCluster;
	}

	protected static String getKafkaTopic() {
		return TOPIC;
	}

}