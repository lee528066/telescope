package com.lee.ts.es.service;

import com.lee.ts.es.bean.Binlog;
import com.lee.ts.es.condition.EsSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author liwei
 * @date 2019-07-30 19:02
 */
public interface EsBinlogService {

    void batchAdd(List<Binlog> logs);

    void add(Binlog log);

    void deleteById(String id);

    Page<Binlog> findByXid(String xid, Pageable pageable);

    Page<Binlog> findByTable(String table, Pageable pageable);

    Page<Binlog> findByCondition(EsSearchCondition esSearchCondition, Pageable pageable);
}
