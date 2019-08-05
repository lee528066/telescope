package com.lee.ts.event.listener;

import com.lee.ts.event.BaseEvent;

/**
 * @author liwei
 * @date 2019-08-02 17:19
 */
public interface EventListener<E extends BaseEvent> {

    /**
     * listener订阅事件
     * @param event 事件
     */
    void onEvent(E event);

    /**
     * 该方法指定了listener注册时的顺序。
     * @return order返回越小，越优先执行
     */
    default int order(){return Integer.MAX_VALUE;}
}
