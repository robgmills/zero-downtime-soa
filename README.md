# Zero Downtime OSGi Micro-services 

## Guide
### Basic OSGi Micro-services
#### Step 0 - Get the dependencies
Simply execute the `bootstrap.sh` script to download and install all the
necessary dependencies including:

- Apache Karaf 3.0.3
- Apache Kafka 2.10-0.8.2.0

Dependencies are downloaded to an `opt/` directory in the project root.

#### Step 1 - Build the project
`mvn clean package`

#### Step 2 - Install the Karaf dependencies
`cp zero-downtime-soa-dependencies/target/zero-downtime-soa-dependencies.kar opt/karaf/deploy/`

This step pre-installs the necessary Karaf bundles for Cellar, Camel, Hazelcast, and Kafka 
required by the bundles.

#### Step 3 - Install the API and Service bundles
    cp hello-service-api/target/hello-service-api.jar opt/karaf/deploy/
    cp hello-service/target/hello-service.jar opt/karaf/deploy/

#### Step 4 - Start Karaf
`opt/karaf/bin/karaf`

#### Step 5 - Call the API
When the API bundle starts, it finds an available port on the host to bind to the Jetty server to.
Check the Karaf log for which port it started on.  You should see a message similar to this:

> 2015-04-04 19:17:22,695 | INFO  | raf-3.0.3/deploy | AbstractConnector                | 82 - org.eclipse.jetty.aggregate.jetty-all-server - 8.1.15.v20140411 | Started SelectChannelConnector@0.0.0.0:<b>59953</b>

Hint: If you can find the port, just execute the following command from the Karaf console:

`log:tail | grep rest-api`


In this case, the Jetty server started on port 59953.  Simply make an HTTP GET request to the API
 endpoint:

`curl -XGET localhost:59953/rest-api/sync/John`

And you should get back a personalized message.

### Distributed OSGi
#### Step 0 - Create slave Karaf instances from the main Karaf instance
From the Karaf console:

    instance:clone root slave-0
    instance:start slave-0
    instance:clone root slave-1
    instance:start slave-1

_Cloning_ the root Karaf instance copies all the current configuration and deployed bundles to 
the slaves.  It's a good way to spin up new instances in real-time to add capacity without 
needing to re-install everything.

### Step 1 - Copy the Cellar config into the root Karaf instance
`cp config/karaf/org.apache.karaf.cellar.node.cfg opt/karaf/etc/`

The config contained in this project enables the bundle, config,
and feature listeners that are responsible for replicating changes 
from the root Karaf instance to the slaves:

    bundle.listener = true
    config.listener = true
    feature.listener = true

### Decoupled & Distributed Routes
#### Step 0 - Start Zookeeper and Kafka
There are helpful scripts in the root of the project to start both a basic, single node 
Zookeeper instance and a 2-node Kafka cluster:

    ./0-start-zookeeper.sh
    ./1-start-kafka-1.sh
    ./1-start-kafka-2.sh

Please note, it's important to start Zookeeper first as Kafka is heavily dependent on it.

#### Step 1 - Call the Kafka-backed API
`curl -XGET localhost:59953/rest-api/async/Mary`

## Documentation
### Karaf
Please refer to the [Karaf](http://karaf.apache.org/manual/latest/quick-start.html) site for current documentation.
### Kafka
Please refer to the [Kafka](http://kafka.apache.org/documentation.html) site for current documentation.
