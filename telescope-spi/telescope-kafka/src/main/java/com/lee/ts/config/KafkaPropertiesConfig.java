package com.lee.ts.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author liwei
 * @date 2019-07-30 13:56
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "spring.kafka")
public class KafkaPropertiesConfig {

    private String bootstrapServers;

    private Consumer consumer = new Consumer();

    @Getter
    @Setter
    public static class Consumer {

        private Integer concurrency;

        private Boolean batchListener;

        private Boolean enableAutoCommit;

        private Integer maxPollRecords;

        private Class keyDeserializer;

        private Class valueDeserializer;
    }
}
