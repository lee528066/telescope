package com.lee.ts.es.component.splitstrategy;

import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;

import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * @author liwei
 * @date 2019-09-07 15:48
 */
@Slf4j
public abstract class AbstractIndexSplitStrategy implements IIndexSplitStrategy{

    private final static String DAY = "day";

    private final static String MONTH = "month";

    private final static String YEAR = "year";

    private final static String HOUR = "hour";

    private static Set<String> indicesCache = new TreeSet<>();

    public String getCurIndex(String strategyType){
        String currentIndexName = getCurrentIndexName(strategyType);
        indicesCache.add(currentIndexName);
        return currentIndexName;
    }

    public String getIndices() {
        return indicesCache.stream()
                .filter(index -> index.startsWith(getIndexPrefix()))
                .collect(Collectors.joining(","));
    }

    /**
     * 初始化所有的ES索引缓存
     * @param indexNames 索引集合
     */
    public synchronized void initIndicesCache(Set<String> indexNames){
        indicesCache.addAll(indexNames);
        log.info("初始化indicesCache成功");
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
        return getIndexPrefix() + currentIndexSuffix;
    }

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
}
