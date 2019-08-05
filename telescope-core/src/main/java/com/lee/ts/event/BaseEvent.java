package com.lee.ts.event;

import lombok.Getter;
import lombok.Setter;

import java.util.EventObject;

/**
 * @author liwei
 * @date 2019-08-02 16:58
 */
@Getter
@Setter
public class BaseEvent extends EventObject {

    private Object value;

    /**
     * 时间发生的时间戳
     */
    private long ts = System.currentTimeMillis();

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    protected BaseEvent(Object source) {
        super(source);
    }

    protected BaseEvent(Object source, Object value) {
        super(source);
        this.value = value;
    }
}
