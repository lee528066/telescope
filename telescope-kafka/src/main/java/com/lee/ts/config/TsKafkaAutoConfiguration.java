package com.lee.ts.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;

import javax.annotation.Resource;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@EnableConfigurationProperties(KafkaPropertiesConfig.class)
public class TsKafkaAutoConfiguration {

    @Resource
    private KafkaPropertiesConfig kafkaPropertiesConfig;

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(kafkaPropertiesConfig.getConsumer().getConcurrency());
        factory.setBatchListener(kafkaPropertiesConfig.getConsumer().getBatchListener());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
    }

    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>(16);
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaPropertiesConfig.getBootstrapServers());
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, getHostName() + "-maxwell-consumer");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, kafkaPropertiesConfig.getConsumer().getEnableAutoCommit());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, kafkaPropertiesConfig.getConsumer().getValueDeserializer());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, kafkaPropertiesConfig.getConsumer().getKeyDeserializer());
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, kafkaPropertiesConfig.getConsumer().getMaxPollRecords());
        return props;
    }

    private String getHostName(){
        try {
            InetAddress addr = InetAddress.getLocalHost();
            return addr.getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return "";
    }
}
