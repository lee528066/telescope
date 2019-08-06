package com.lee.ts.es.component;

import org.joda.time.DateTime;

/**
 * @author liwei
 * @date 2019-08-06 19:33
 */
public class IndexDateExpression {

    public static String getDateStr(){
        return new DateTime().toString("yyyy-MM-dd");
    }
}
