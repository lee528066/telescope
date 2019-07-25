# telescope

# TODO LIST
1. 接入mysql用于统计DML|DDL频率
2. 接入hbase用于备份历史数据
    1. 后期考虑dump出insert语句用于数据回滚
3. 接入elk提供日志查询
4. 引入事件驱动的设计模式来实现以上功能。包括定时的异步任务框架

目前maxwell是针对单个mysql实例的，所以json报˚文中无法看出是哪个mysql数据库（ip）需要定制maxwell