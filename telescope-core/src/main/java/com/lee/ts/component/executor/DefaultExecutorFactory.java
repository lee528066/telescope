package com.lee.ts.component.executor;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author liwei
 * @date 2019-08-02 16:37
 */
@Component
public class DefaultExecutorFactory implements ExecutorFactory {

    @Override
    public ThreadPoolTaskExecutor getExecutor() {
        ThreadPoolTaskExecutor taskTreadPool = new ThreadPoolTaskExecutor();
        taskTreadPool.setCorePoolSize(Runtime.getRuntime().availableProcessors());
        taskTreadPool.setMaxPoolSize(100);
        taskTreadPool.setQueueCapacity(2000);
        taskTreadPool.setKeepAliveSeconds(1);
        //TODO 临时使用这个策略
        taskTreadPool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return taskTreadPool;
    }
}
