package com.lee.ts.es.component.splitstrategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * elasticsearch index 按时间切分的策略
 * @author liwei
 * @date 2019-08-06 19:33
 */
@Slf4j
@Component("binlogSplitStrategy")
public class BinlogSplitStrategy extends AbstractIndexSplitStrategy {

    private final static String PREFIX = "mysqlbinlog-";

    @Override
    public String getIndexPrefix() {
        return PREFIX;
    }

}
