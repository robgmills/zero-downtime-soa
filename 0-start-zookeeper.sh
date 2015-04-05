#!/bin/bash
OPT=opt
KAFKA=kafka
$OPT/$KAFKA/bin/zookeeper-server-start.sh $OPT/$KAFKA/config/zookeeper.properties
