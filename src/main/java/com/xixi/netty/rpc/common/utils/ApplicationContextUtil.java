package com.xixi.netty.rpc.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author shengchengchao
 * @Description
 * @createTime 2021/9/15
 */
@Slf4j
@Component
public class ApplicationContextUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext arg0) throws BeansException {
        applicationContext = arg0;
    }


    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static <T> T getBean(String name) {
        try {
            Object bean = applicationContext.getBean(name);
            return (T) bean;
        } catch (Exception e) {
            log.error(" ApplicationContextUtil.getBean 出现问题  ", e);
        }
        return null;
    }

    public static <T> T getBeanByClass(Class<?> clazz) {
        try {
            return (T) applicationContext.getBean(clazz);
        } catch (Exception e) {
            log.error(" ApplicationContextUtil.getBeanByClass 出现问题  ", e);
        }
        return null;
    }
}
