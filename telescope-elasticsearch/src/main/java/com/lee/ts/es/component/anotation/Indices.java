package com.lee.ts.es.component.anotation;

import java.lang.annotation.*;

/**
 * @author liwei
 * @date 2019-08-07 17:07
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Indices {
    String indexName();
}
