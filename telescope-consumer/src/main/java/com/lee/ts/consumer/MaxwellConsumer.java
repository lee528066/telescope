package com.lee.ts.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.listener.BatchAcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author liwei
 */
@Component
@Slf4j
public class MaxwellConsumer implements BatchAcknowledgingMessageListener<String, String> {

//    @Override
//    @KafkaListener(containerFactory = "kafkaListenerContainerFactory", topics = "maxwell")
//    public void onMessage(ConsumerRecord<String, String> data, Acknowledgment acknowledgment) {
//        log.info("message:{}", data.value());
//
//    }

    @Override
    @KafkaListener(containerFactory = "kafkaListenerContainerFactory", topics = "maxwell")
    public void onMessage(List<ConsumerRecord<String, String>> data, Acknowledgment acknowledgment) {
        for (ConsumerRecord<String, String> datum : data) {
            log.info("message:{}", datum.value());
        }
        acknowledgment.acknowledge();
    }
}
