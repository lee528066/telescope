package com.lee.ts.es.component;

import com.lee.ts.es.component.anotation.EsCondition;
import com.lee.ts.es.component.anotation.EsQueryType;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.AbstractQueryBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.Objects;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * 将ES搜索条件组装成ES API的Query的对象
 * (注意：传入的condition对象必须被{@link EsCondition}注解修饰)
 * @author liwei
 * @date 2019-08-01 14:52
 */
@Slf4j
@Component
public class ConditionQueryBuilder {

    public <T> QueryBuilder builder(T condition) {
        BoolQueryBuilder boolQuery = boolQuery();
        Class clazz = condition.getClass();
        Annotation esCondition = clazz.getAnnotation(EsCondition.class);
        if (Objects.isNull(esCondition)) {
            throw new RuntimeException("该condition未标记@EsCondition注解");
        }
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field field : declaredFields) {
            EsQueryType esQueryType = field.getAnnotation(EsQueryType.class);
            if (Objects.nonNull(esQueryType)) {
                boolQuery = appendStrongOperation(boolQuery, esQueryType, field, condition);
            }
        }
        return boolQuery;
    }

    private <T> BoolQueryBuilder appendStrongOperation(BoolQueryBuilder boolQuery, EsQueryType esQueryType,
                                                Field field, T condition) {
        AbstractQueryBuilder abstractQueryBuilder = buildQuery(esQueryType, field, condition);
        if (Objects.nonNull(abstractQueryBuilder)) {
            switch (esQueryType.strongType()) {
                case MUST:
                    return boolQuery.must(abstractQueryBuilder);
                case MUST_NOT:
                    return boolQuery.mustNot(abstractQueryBuilder);
                case FILTER:
                    return boolQuery.filter(abstractQueryBuilder);
                case SHOULD:
                    return boolQuery.should(abstractQueryBuilder);
            }
        }
        return boolQuery;
    }

    private <T> AbstractQueryBuilder buildQuery(EsQueryType esQueryType, Field field, T condition) {
        Object val = fieldVal(field, condition);
        if(Objects.isNull(val)){
            return null;
        }
        switch (esQueryType.type()) {
            case TERM:
                return termQuery(esQueryType.field(), val);
            case MATCH:
                return matchQuery(esQueryType.field(), val);
            case RANGE:
                return buildRangeQueryBuilder(esQueryType, val);
        }
        throw new RuntimeException("未匹配到正确的EsQueryType");
    }

    private RangeQueryBuilder buildRangeQueryBuilder(EsQueryType esQueryType, Object val) {
        RangeQueryBuilder rangeQueryBuilder = rangeQuery(esQueryType.field());
        switch (esQueryType.rangeOperation()) {
            case LT:
                rangeQueryBuilder.lt(val);
                break;
            case LTE:
                rangeQueryBuilder.lte(val);
                break;
            case GT:
                rangeQueryBuilder.gt(val);
                break;
            case GTE:
                rangeQueryBuilder.gte(val);
                break;
            case FROM:
                if(val instanceof Date){
                    rangeQueryBuilder.from(((Date)val).getTime());
                }else{
                    rangeQueryBuilder.from(val);
                }
                break;
            case TO:
                if(val instanceof Date){
                    rangeQueryBuilder.to(((Date)val).getTime());
                }else{
                    rangeQueryBuilder.to(val);
                }
                break;
            case NONE:
                break;
        }
        return rangeQueryBuilder;
    }

    private <T> Object fieldVal(Field field, T condition) {
        field.setAccessible(true);
        try {
            return field.get(condition);
        } catch (IllegalAccessException e) {
            log.error(String.format("field:%s 反射取值发生异常", field.getName()), e);
            throw new RuntimeException(e);
        }finally {
            field.setAccessible(false);
        }
    }

}
