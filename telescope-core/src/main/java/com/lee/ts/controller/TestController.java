package com.lee.ts.controller;

import com.alibaba.fastjson.JSON;
import com.lee.ts.es.bean.Binlog;
import com.lee.ts.es.condition.EsSearchCondition;
import com.lee.ts.es.service.BinlogService;
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
@RequestMapping("/test")
public class TestController {

    @Resource
    private BinlogService binlogService;

    @RequestMapping("/log/add")
    public String addBinlog(){
        Binlog binlog = JSON.parseObject(json, Binlog.class);
        binlogService.add(binlog);
        return "succ";
    }

    @RequestMapping("/byTable/find")
    public Object findByTable(String table, @PageableDefault(sort = "ts") Pageable pageable){
        return binlogService.findByTable(table, pageable);
    }

    @GetMapping("/condition/find")
    public Object findByCondition(EsSearchCondition condition, @PageableDefault(sort = "ts") Pageable pageable){
        return binlogService.findByCondition(condition, pageable);
    }

    private static final String json = "{\"database\":\"wms\",\"table\":\"auth_user\",\"type\":\"bootstrap-insert\",\"ts\":1563957082,\"data\":{\"id\":327,\"user_name\":\"谢媛1\",\"password\":\"+itT6Kd91zydCjZdt9s3o2DLE0AdERz8pUSMOT75FD/efLgQLtEzpF1tv9OflNZN\",\"is_available\":1,\"account\":\"xiey\",\"user_email\":null,\"user_phone\":null,\"create_user_id\":0,\"create_time\":\"2017-09-04 15:53:22\",\"update_time\":\"2017-09-18 17:41:23\",\"warehouse_id\":2,\"user_auth_version\":2}}\n";
}
