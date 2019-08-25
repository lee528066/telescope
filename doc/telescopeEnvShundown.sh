#!/bin/bash
NAMES=('Maxwell' 'kibana' 'Elasticsearch' 'kafkaServer' 'zookeeper')
for NAME in ${NAMES[@]}
do
	echo "---------------"
	echo $NAME
	ID=`ps -ef | grep "$NAME" | grep -v "$0" | grep -v "grep" | awk '{print $2}'`
	if [ -n "$ID" ]
	then
		for id in $ID
			do
			echo "killing $name: $id"
			kill -9 $id
			echo "killed $id"
			sleep 5s
			done
	fi
done
echo "---------------- all killed"