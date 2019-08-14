package com.lee.ts.es.repository;

import com.lee.ts.es.bean.Binlog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author liwei
 * @date 2019-07-30 18:57
 */
public interface BinlogRepository extends ElasticsearchRepository<Binlog, String> {

    Page<Binlog> findByXid(String xid, Pageable pageable);

    Page<Binlog> findByTable(String table, Pageable pageable);

}
