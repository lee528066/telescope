package com.lee.ts.controller;

import com.lee.ts.es.condition.EsSearchCondition;
import com.lee.ts.es.service.EsBinlogService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author liwei
 * @date 2019-07-31 11:24
 */
@RestController
@RequestMapping("/es/binlog")
public class BinlogSearchController {

    @Resource
    private EsBinlogService esBinlogService;

    @GetMapping("/find_by_table")
    public Object findByTable(String table, @PageableDefault(sort = "ts") Pageable pageable){
        return esBinlogService.findByTable(table, pageable);
    }

    @GetMapping("/find_by_condition")
    public Object findByCondition(EsSearchCondition condition, @PageableDefault(sort = "ts") Pageable pageable){
        return esBinlogService.findByCondition(condition, pageable);
    }

    @GetMapping("/indices/find_by_condition")
    public Object findByConditionInIndices(EsSearchCondition condition, @PageableDefault(sort = "ts") Pageable pageable){
        return esBinlogService.findByConditionIndices(condition, pageable);
    }

}
