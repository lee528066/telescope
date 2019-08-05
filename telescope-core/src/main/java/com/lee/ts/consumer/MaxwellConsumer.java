package com.lee.ts.consumer;

import com.lee.ts.event.EventMultiCaster;
import com.lee.ts.event.SyncEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.BatchAcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author liwei
 */
@Slf4j
@Component
public class MaxwellConsumer implements BatchAcknowledgingMessageListener<String, String> {

    @Resource
    private EventMultiCaster eventMulticaster;

    @Override
    @KafkaListener(containerFactory = "kafkaListenerContainerFactory", groupId = "telescope.consumer", topics = "maxwell", autoStartup = "false")
    public void onMessage(List<ConsumerRecord<String, String>> dataRecords, Acknowledgment acknowledgment) {
        for (ConsumerRecord<String, String> dataRecord : dataRecords) {
            log.info("key:{} message:{}", dataRecord.key(), dataRecord.value());
            eventMulticaster.publish(new SyncEvent(this, dataRecord.value()));
        }
        acknowledgment.acknowledge();
    }
}
