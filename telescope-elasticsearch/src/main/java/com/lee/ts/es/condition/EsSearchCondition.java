package com.lee.ts.es.condition;

import com.lee.ts.es.compoment.Enum.QueryType;
import com.lee.ts.es.compoment.Enum.RangeOperation;
import com.lee.ts.es.compoment.anotation.EsCondition;
import com.lee.ts.es.compoment.anotation.EsQueryType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author liwei
 * @date 2019-07-31 13:38
 */
@Setter
@Getter
@EsCondition
public class EsSearchCondition {

    /**
     * 数据库名
     */
    @EsQueryType(type = QueryType.TERM, field = "database")
    private String database;

    /**
     * mysql表名
     */
    @EsQueryType(type = QueryType.TERM, field = "table")
    private String table;

    /**
     * 数据DML操作类型，如：insert，update等
     */
    @EsQueryType(type = QueryType.TERM, field = "type")
    private String type;

    /**
     * sql执行时间点
     */
    @EsQueryType(type = QueryType.RANGE, rangeOperation = RangeOperation.FROM, field = "happenTime")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date happenTimeStart;

    /**
     * sql执行时间点
     */
    @EsQueryType(type = QueryType.RANGE, rangeOperation = RangeOperation.TO, field = "happenTime")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date happenTimeEnd;

    /**
     * 事务xid
     */
    @EsQueryType(type = QueryType.TERM, field = "xid")
    private String xid;

    @EsQueryType(type = QueryType.MATCH, field = "data")
    private String dataKeyword;
}
