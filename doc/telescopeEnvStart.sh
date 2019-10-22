#!/bin/bash
nohup zkServer start > ~/log/zkServer.file 2>&1 &
echo '------zkServer started'

sleep 10s
nohup kafka-server-start /usr/local/etc/kafka/server.properties > ~/log/kafkaServer.file 2>&1 & 
echo '------kafkaServer started'

sleep 5s
nohup elasticsearch > ~/log/elasticsearch.file 2>&1 & 
echo '------elasticsearch started'

sleep 5s
nohup kibana > ~/log/kibana.file 2>&1 &
echo '------kibana started'

sleep 10s
nohup maxwell --config /usr/local/etc/maxwell/config.properties > ~/log/maxwell.file 2>&1 &
echo '------maxwell started'


echo "---------------------- start success"