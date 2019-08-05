package com.lee.ts.component.executor;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @author liwei
 * @date 2019-08-02 16:35
 */
public interface ExecutorFactory {
    ThreadPoolTaskExecutor getExecutor();
}
