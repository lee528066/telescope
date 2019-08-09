package com.lee.ts.es.component;

import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.TreeSet;

/**
 * elasticsearch index 按时间切分的策略
 * @author liwei
 * @date 2019-08-06 19:33
 */
@Slf4j
@Component("indexSplitStrategy")
public class IndexSplitStrategy{

    private static final String PREFIX = "mysqlbinlog-";

    private static final String DAY = "day";

    private static final String MONTH = "month";

    private static final String YEAR = "year";

    private static final String HOUR = "hour";

    private String day(){
        return new DateTime().toString("yyyy-MM-dd");
    }

    private String month(){
        return new DateTime().toString("yyyy-MM");
    }

    private String year(){
        return new DateTime().toString("yyyy");
    }

    private String hour(){
        return new DateTime().toString("yyyy-MM-dd-HH");
    }

    private static Set<String> indicesCache = new TreeSet<>();

    public String getCurIndex(String strategyType){
        String currentIndexName = getCurrentIndexName(strategyType);
        indicesCache.add(currentIndexName);
        return currentIndexName;
    }

    public String getIndices() {
        return String.join(",", indicesCache);
    }

    private String getCurrentIndexName(String strategyType) {
        String currentIndexSuffix = null;
        switch (strategyType) {
            case DAY:
                currentIndexSuffix = day();
                break;
            case HOUR:
                currentIndexSuffix = hour();
                break;
            case MONTH:
                currentIndexSuffix = month();
                break;
            case YEAR:
                currentIndexSuffix = year();
                break;
        }
        return PREFIX + currentIndexSuffix;
    }

    /**
     * 初始化所有的ES索引缓存
     * @param indexNames 索引集合
     */
    public synchronized void initIndicesCache(Set<String> indexNames){
        indicesCache.addAll(indexNames);
        log.info("初始化indicesCache成功");
    }
}
