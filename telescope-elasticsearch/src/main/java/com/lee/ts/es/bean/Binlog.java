package com.lee.ts.es.bean;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.lee.ts.es.component.IndexSplitStrategy;
import com.lee.ts.es.component.anotation.Indices;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

/**
 * 根据{@link IndexSplitStrategy#getCurIndex(String)}获取当前的index
 * 根据{@link IndexSplitStrategy#getIndices()}获取所有的index
 * @author liwei
 * @date 2019-07-30 17:17
 */
@Data
@Document(indexName = "#{ indexSplitStrategy.getCurIndex('month') }", type = "binlog")
@Indices(indexName = "#{ indexSplitStrategy.getIndices() }")
public class Binlog {

    @Id
    private String id;

    /**
     * 数据库名
     */
    @Field(type= FieldType.Keyword)
    private String database;

    /**
     * mysql表名
     */
    @Field(type= FieldType.Keyword)
    private String table;

    /**
     * 数据DML操作类型，如：insert，update等
     */
    @Field(type= FieldType.Keyword)
    private String type;

    /**
     * sql执行时间戳(10位时间戳)
     */
    @Field(type= FieldType.Long)
    private Long ts;

    /**
     * sql执行时间点
     */
    @Field(type= FieldType.Date)
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date happenTime;

    /**
     * 事务xid
     */
    private String xid;

    private Boolean commit;

    @Field(type= FieldType.Text)
    private String data;

    @Field(type= FieldType.Text)
    private String old;

    public static Binlog fromJsonStr(String jsonStr){
        return JSON.parseObject(jsonStr, Binlog.class);
    }
}
