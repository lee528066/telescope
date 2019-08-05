package com.lee.ts.event.listener;

import com.lee.ts.event.SyncEvent;
import org.springframework.stereotype.Component;

/**
 * @author liwei
 * @date 2019-08-05 20:46
 */
@Component
public class HbaseSyncListener implements EventListener<SyncEvent> {

    @Override
    public void onEvent(SyncEvent event) {

    }
}
