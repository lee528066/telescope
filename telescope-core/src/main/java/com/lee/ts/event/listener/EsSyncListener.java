package com.lee.ts.event.listener;

import com.lee.ts.es.bean.Binlog;
import com.lee.ts.es.service.EsBinlogService;
import com.lee.ts.event.SyncEvent;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author liwei
 * @date 2019-08-02 17:22
 */
@Component
public class EsSyncListener implements EventListener<SyncEvent> {

    @Resource
    private EsBinlogService esBinlogService;

    @Override
    public void onEvent(SyncEvent event) {
        String json = (String) event.getValue();
        esBinlogService.add(Binlog.fromJsonStr(json));
    }

    @Override
    public int order(){
        return Integer.MIN_VALUE;
    }
}
