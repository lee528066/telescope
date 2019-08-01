package com.lee.ts.es.service.impl;

import com.lee.ts.es.bean.Binlog;
import com.lee.ts.es.compoment.ConditionQueryBuilder;
import com.lee.ts.es.condition.EsSearchCondition;
import com.lee.ts.es.repository.BinlogRepository;
import com.lee.ts.es.service.BinlogService;
import org.apache.commons.collections.CollectionUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * @author liwei
 * @date 2019-07-30 19:04
 */
@Service
public class BinlogServiceImpl implements BinlogService {

    @Resource
    private BinlogRepository mysqlBinlogRepository;

    @Resource
    private ElasticsearchTemplate elasticsearchTemplate;

    @Resource
    private ConditionQueryBuilder conditionQueryBuilder;

    @Override
    public void batchAdd(List<Binlog> logs) {
        if (CollectionUtils.isEmpty(logs)) {
            return;
        }
        List<IndexQuery> indexQueries = logs.stream().map(log -> {
                    IndexQuery indexQuery = new IndexQuery();
                    indexQuery.setObject(log);
                    return indexQuery;
                }
        ).collect(Collectors.toList());
        elasticsearchTemplate.bulkIndex(indexQueries);
    }

    @Override
    public void add(Binlog log) {
        log.setHappenTime(new Date(log.getTs() * 1000));
        mysqlBinlogRepository.save(log);
    }

    @Override
    public void deleteById(String id) {
        mysqlBinlogRepository.deleteById(id);
    }

    @Override
    public Page<Binlog> findByXid(String xid, Pageable pageable) {
        return mysqlBinlogRepository.findByXid(xid, pageable);
    }

    @Override
    public Page<Binlog> findByTable(String table, Pageable pageable) {
        return mysqlBinlogRepository.findByTable(table, pageable);
    }

    @Override
    public Page<Binlog> findByCondition(EsSearchCondition esSearchCondition, Pageable pageable) {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withFilter(conditionQueryBuilder.builder(esSearchCondition))
                .withPageable(pageable)
                .build();
        return elasticsearchTemplate.queryForPage(searchQuery, Binlog.class);
    }
}
