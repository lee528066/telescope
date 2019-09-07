package com.lee.ts.es.component.splitstrategy;

/**
 * @author liwei
 * @date 2019-09-07 15:42
 */
public interface IIndexSplitStrategy {

    String getCurIndex(String strategyType);

    String getIndices();

    String getIndexPrefix();
}
