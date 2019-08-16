# telescope

## 介绍：
telescope(望远镜)是一个基于java实现的RDS数据变化查询平台。通过监听关系型数据库动态变化kafka消息——一般是格式化的日志数据（如:mysql的binlog日志，postgresql的wal逻辑日志），将json化的日志同步到elasticsearch集群中。
通过kibana查询相关的数据源级，表级别，事务级别，row级别的变化记录。telescope本身也提供了restful风格的http请求，自定义查询api反馈对应的分页结果。

## 价值：
1. 帮助解决复杂业务场景下的数据运维难题，追溯数据变化和数据还原。
2. 对传统RDS的DML频率统计
3. 数据异构，监听不同表的rows变化，自己封装组成对应新的数据，提供自己新的api查询接口
4. 提供新的缓存思路（redis等）。对部分延迟要求不太高的数据，提供对业务工程零侵入的方式缓存数据的方式
5. 为row级别的数据监控提供可能性，可以针对部分业务表row变化，定制自己的监控和校验逻辑。(例如：订单状态更新为发运后，校验实际发运的数量和库存交易日志是否匹配等等)
6. 无入侵式的备份rds历史数据提供可能性（mysql单表数据量不建议超过500w左右）。mysql->hbase：备份无用的历史数据（如：历史订单）；mysql->elasticsearch: 提供更强的实时查询能力（如：大量的库存交易流水）
7. 等等任何你能想到的价值...

## 架构设计（图片待补充）
1. mysql binlog解析通过[maxwell](https://github.com/zendesk/maxwell.git)中间件实现，将json化的row数据同步到kafka对应的topic（maxwell也支持其他多种数据源）
2. postgresql wal逻辑日志解析通过 hellobike [amazonriver](https://github.com/lee528066/amazonriver.git) 的中间件实现, 将json化的row数据同步到kafka对应的topic
注意: 这里是我自己fork的项目，主要修改了wal逻辑复制过程中对old-keys数据的扩展记录。原项目并不支持old-keys。git地址：[amazonriver](https://github.com/hellobike/amazonriver.git)
3. 由于amazonriver的项目是go语言实现，而且对于生成的json格式row数据，无法解析出xid（事务id）。最近在开始自己实现java版本的 walLogTojson 的项目。[elephant](https://github.com/lee528066/elephant.git)

## 特性
1. 基于spring-data-elasticsearch和spring的spel表达式，实现了es的index自动按时间分片（这部分实现可能没有Logstash强大），解决索引数据可能膨胀过快的问题，为按照时间维度删除index提供基础。减少后期elasticsearch的reindex的复杂操作次数。
2. spring-data-elasticsearch的ElasticsearchTemplate只能查询当前指定的索引，且代码实现扩展性差。自实现IndicesElasticsearchTemplate
和@Indices索引，实现查询前缀相同的所有索引。

# TODO LIST
1. postgresql的日志同步, 先实现elephant项目
2. DML频率统计
3. SyncEvent的持久化（解决数据一致性问题）
4. SyncEvent的异步化（增加kafka消息处理的吞吐量）
5. 引入报警机制，监控数据同步的延迟过高的情况
