package com.lee.ts.event;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * elasticsearch同步事件
 * @author liwei
 * @date 2019-08-02 17:06
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SyncEvent extends BaseEvent{

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public SyncEvent(Object source) {
        super(source);
    }

    public SyncEvent(Object source, Object value) {
        super(source, value);
    }
}
