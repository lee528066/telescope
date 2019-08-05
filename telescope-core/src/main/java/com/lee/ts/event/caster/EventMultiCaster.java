package com.lee.ts.event.caster;

import com.lee.ts.event.BaseEvent;
import com.lee.ts.event.listener.EventListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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
public class EventMultiCaster {

    @Resource
    private ApplicationContext applicationContext;

    private Map<Class<BaseEvent>, List<EventListener>> listenerCache = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public void publish(BaseEvent event) {
        List<EventListener> eventListeners = listenerCache.get(event.getClass());
        Assert.notEmpty(eventListeners, String.format("event: %s 未正常注册到listenerCache中", event.getClass()));
        eventListeners.forEach(eventListener -> eventListener.onEvent(event));
    }

    /**
     * 将所有的{@link EventListener}注册到listenerCache中
     */
    @PostConstruct
    public void registerListener() {
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
                    ParameterizedType genericInterface = (ParameterizedType) eventListener.getClass().getGenericInterfaces()[0];
                    Type actualEventType = genericInterface.getActualTypeArguments()[0];
                    Class eventClazz = (Class) actualEventType;
                    Assert.isTrue(eventClazz != null && BaseEvent.class.isAssignableFrom(eventClazz),
                            "该eventListener的onEvent方法参数不正确");
                    return eventClazz;
                }));
    }
}
