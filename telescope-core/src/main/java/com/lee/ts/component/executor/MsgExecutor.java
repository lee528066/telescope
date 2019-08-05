package com.lee.ts.component.executor;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author liwei
 * @date 2019-08-02 16:16
 */
@Component
public class MsgExecutor{

    private ThreadPoolTaskExecutor taskTreadPool;

    @Resource
    private DefaultExecutorFactory defaultExecutorFactory;

    @PostConstruct
    public void init(){
        taskTreadPool = defaultExecutorFactory.getExecutor();
        taskTreadPool.initialize();
    }

    public void execute(){

    }
}
