package com.lee.ts.hbase.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author liwei
 * @date 2019-07-30 10:03
 */
@Getter
@Setter
@ConfigurationProperties("hbase")
public class HbasePropertiesConfig {

    private Zookeeper zookeeper = new Zookeeper();

    public static class Zookeeper {
        /**
         * zookeeper集群节点
         */
        private String quorum;

        /**
         * zookeeper客户端port
         */
        private String propertyClientPort;

        /**
         * hbase服务在zookeeper中的根节点
         */
        private String nodeParent;

        public String getQuorum() {
            return quorum;
        }

        public void setQuorum(String quorum) {
            this.quorum = quorum;
        }

        public String getPropertyClientPort() {
            return propertyClientPort;
        }

        public void setPropertyClientPort(String propertyClientPort) {
            this.propertyClientPort = propertyClientPort;
        }

        public String getNodeParent() {
            return nodeParent;
        }

        public void setNodeParent(String nodeParent) {
            this.nodeParent = nodeParent;
        }
    }
}
