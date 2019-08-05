package com.lee.ts.event;

import com.lee.ts.event.listener.EventListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;

/**
 * @author liwei
 * @date 2019-08-02 17:08
 */
@Slf4j
@Component
public class EventMultiCaster{

    @Resource
    private ApplicationContext applicationContext;

    private final String ON_EVENT_METHOD = "onEvent";

    private Map<Class<BaseEvent>, List<EventListener>> listenerCache = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public void publish(BaseEvent event){
        List<EventListener> eventListeners = listenerCache.get(event.getClass());
        Assert.notEmpty(eventListeners, String.format("event: %s 未正常注册到listenerCache中", event.getClass()));
        eventListeners.forEach(eventListener -> eventListener.onEvent(event));
    }

    /**
     * 将所有的{@link EventListener}注册到listenerCache中
     */
    @PostConstruct
    public void registerListener(){
        String[] names = applicationContext.getBeanNamesForType(EventListener.class);

        List<EventListener> eventListeners = Stream.of(names)
                .map(name -> (EventListener) applicationContext.getBean(name))
                .collect(Collectors.toList());

        buildListenerCache(eventListeners);

        sortListenersInCache();
    }

    private void sortListenersInCache() {
        listenerCache.forEach((k, v) -> v = v.stream()
                .sorted(Comparator.comparing(EventListener::order))
                .collect(Collectors.toList()));
    }

    @SuppressWarnings("unchecked")
    private void buildListenerCache(List<EventListener> eventListeners) {
        listenerCache = eventListeners.stream()
                .collect(groupingBy(eventListener -> {
                    Class<BaseEvent> parameterType = null;
                    try {
                        Method onEventMethod = eventListener.getClass().getMethod(ON_EVENT_METHOD);
                        parameterType = (Class<BaseEvent>) onEventMethod.getParameterTypes()[0];
                    } catch (NoSuchMethodException e) {
                        log.error("注册EventListener发生异常:", e);
                    }
                    Assert.isTrue(parameterType != null && BaseEvent.class.isAssignableFrom(parameterType),
                            "该eventListener的onEvent方法参数不正确");
                    return parameterType;
                }));
    }
}
