#!/bin/bash

clear_opt() {
	rm -r opt/
}

install_kafka() {
	mkdir opt
	wget -O opt/kafka.tgz http://mirror.reverse.net/pub/apache/kafka/$2/kafka_$1-$2.tgz
	tar -C opt/ -xf opt/kafka.tgz
	ln -s kafka_$1-$2 opt/kafka
	rm opt/kafka.tgz
}

install_karaf() {
	mkdir opt
	wget -O opt/karaf.tgz http://apache.mirrors.lucidnetworks.net/karaf/$1/apache-karaf-$1.tar.gz
	tar -C opt/ -xf opt/karaf.tgz
	ln -s apache-karaf-$1 opt/karaf
	rm opt/karaf.tgz
}

clear_opt
install_kafka 2.10 0.8.2.0
install_karaf 3.0.3
