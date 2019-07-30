package com.lee.ts.hbase.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.concurrent.Executors;


/**
 * @author liwei
 * @date 2019-07-30 09:54
 */
@Slf4j
@Configuration
@ConditionalOnClass(Connection.class)
@EnableConfigurationProperties(HbasePropertiesConfig.class)
public class TsHbaseAutoConfiguration {

    @Resource
    private HbasePropertiesConfig propertiesConfig;

    @Bean
    public Connection connection(){
        Connection connection = null;
        try {
            connection = ConnectionFactory.createConnection(configuration(propertiesConfig),
                    Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));
        } catch (IOException e) {
            log.error("init hbase connection error, reason:", e);
        }
        return connection;
    }

    public org.apache.hadoop.conf.Configuration configuration(HbasePropertiesConfig propertiesConfig){
        org.apache.hadoop.conf.Configuration conf = HBaseConfiguration.create();
        conf.set(HConstants.ZOOKEEPER_QUORUM, propertiesConfig.getZookeeper().getQuorum());
        conf.set(HConstants.CLIENT_ZOOKEEPER_CLIENT_PORT, propertiesConfig.getZookeeper().getPropertyClientPort());
        conf.set(HConstants.ZOOKEEPER_ZNODE_PARENT, propertiesConfig.getZookeeper().getNodeParent());
        conf.set(HConstants.HBASE_CLIENT_IPC_POOL_SIZE, "10");
        return conf;
    }
}
