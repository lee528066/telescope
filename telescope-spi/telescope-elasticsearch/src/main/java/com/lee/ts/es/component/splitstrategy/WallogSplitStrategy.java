package com.lee.ts.es.component.splitstrategy;

import org.springframework.stereotype.Component;

/**
 * postgresql
 * @author liwei
 * @date 2019-09-07 15:47
 */
@Component("wallogSplitStrategy")
public class WallogSplitStrategy extends AbstractIndexSplitStrategy {

    private final static String PREFIX = "postgresqlwallog-";

    @Override
    public String getIndexPrefix() {
        return PREFIX;
    }
}
