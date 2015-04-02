#!/bin/bash

install_kafka() {
	mkdir opt
	wget -O opt/kafka.tgz http://mirror.reverse.net/pub/apache/kafka/$2/kafka_$1-$2.tgz
	tar -C opt/ -xf opt/kafka.tgz
	ln -s kafka_$1-$2 opt/kafka
	rm opt/kafka.tgz
}

install_kafka 2.10 0.8.2.0
