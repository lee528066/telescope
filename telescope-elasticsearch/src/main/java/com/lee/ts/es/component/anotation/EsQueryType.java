package com.lee.ts.es.component.anotation;


import com.lee.ts.es.component.Enum.QueryType;
import com.lee.ts.es.component.Enum.RangeOperation;
import com.lee.ts.es.component.Enum.StrongType;

import java.lang.annotation.*;

/**
 * @author liwei
 * @date 2019-08-01 14:25
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EsQueryType {

    QueryType type();

    String field();

    RangeOperation rangeOperation() default RangeOperation.NONE;

    /**
     * 默认使用filter，不会计算sorce分值，减少es性能损失
     */
    StrongType strongType() default StrongType.FILTER;
}
