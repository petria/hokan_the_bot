package org.freakz.hokan_ng_springboot.bot.services.service.lunch;

import org.freakz.hokan_ng_springboot.bot.common.enums.LunchPlace;
import org.freakz.hokan_ng_springboot.bot.common.models.LunchData;
import org.freakz.hokan_ng_springboot.bot.services.service.annotation.LunchPlaceHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Petri Airio on 21.1.2016.
 * -
 */
@Service
public class LunchServiceImpl implements LunchService {

    private static final Logger log = LoggerFactory.getLogger(LunchServiceImpl.class);

    @Autowired
    private ApplicationContext applicationContext;

    private boolean findHandlersMethod(LunchPlace lunchPlaceRequest, LunchData response, LocalDateTime day) {
        String[] names = applicationContext.getBeanDefinitionNames();
        for (String beanName : names) {
            Object obj = applicationContext.getBean(beanName);
            Class<?> objClz = obj.getClass();
            if (org.springframework.aop.support.AopUtils.isAopProxy(obj)) {
                objClz = org.springframework.aop.support.AopUtils.getTargetClass(obj);
            }
            for (Method m : objClz.getDeclaredMethods()) {
                if (m.isAnnotationPresent(LunchPlaceHandler.class)) {
                    Annotation annotation = m.getAnnotation(LunchPlaceHandler.class);
                    LunchPlaceHandler lunchPlaceHandler = (LunchPlaceHandler) annotation;
                    LunchPlace lunchPlace = lunchPlaceHandler.LunchPlace();
                    if (lunchPlace == lunchPlaceRequest) {
                        try {
                            log.debug("Method: {} -> {}", m, lunchPlace);
                            m.invoke(obj, lunchPlaceRequest, response, day);
                            return true;
                        } catch (Exception e) {
                            e.printStackTrace();
                            log.error("Could not call service handler for: {}", lunchPlaceRequest);
                            return false;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public LunchData getLunchForDay(LunchPlace place, LocalDateTime day) {
        LunchData lunchData = new LunchData();
        if (!findHandlersMethod(place, lunchData, day)) {
            log.warn("Could find handler for: {}", place);
            return null;
        }
        return lunchData;
    }

    @Override
    public List<LunchPlace> getLunchPlaces() {
        String[] names = applicationContext.getBeanDefinitionNames();
        List<LunchPlace> lunchPlaces = new ArrayList<>();
        for (String beanName : names) {
            Object obj = applicationContext.getBean(beanName);
            Class<?> objClz = obj.getClass();
            if (org.springframework.aop.support.AopUtils.isAopProxy(obj)) {
                objClz = org.springframework.aop.support.AopUtils.getTargetClass(obj);
            }
            for (Method m : objClz.getDeclaredMethods()) {
                if (m.isAnnotationPresent(LunchPlaceHandler.class)) {
                    Annotation annotation = m.getAnnotation(LunchPlaceHandler.class);
                    LunchPlaceHandler lunchPlaceHandler = (LunchPlaceHandler) annotation;
                    LunchPlace lunchPlace = lunchPlaceHandler.LunchPlace();
                    lunchPlaces.add(lunchPlace);
                }
            }
        }
        return lunchPlaces;
    }
}
