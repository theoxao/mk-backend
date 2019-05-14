package com.theoxao.commons;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;

import java.beans.PropertyDescriptor;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractVO<T> {
    private Class<T> clazz;

    @SuppressWarnings("unchecked")
    public AbstractVO() {
        ParameterizedType type = ((ParameterizedType) getClass().getGenericSuperclass());
        clazz = (Class<T>) type.getActualTypeArguments()[0];
    }

    private static void copyProperties(Object source, Object target,
                                       boolean ignoreNullValue, String... ignoreProperties) {
        if (null == source || null == target) {
            throw new IllegalArgumentException(
                    "source and target can not be null!");
        }
        BeanWrapper sourceWrapper = new BeanWrapperImpl(source);
        BeanWrapper targetWrapper = new BeanWrapperImpl(target);
        List<String> ignorePropertiesList = null == ignoreProperties ? new ArrayList<String>(
                0) : Arrays.asList(ignoreProperties);
        for (PropertyDescriptor propertyDescriptor : sourceWrapper
                .getPropertyDescriptors()) {
            String propertyName = propertyDescriptor.getName();
            if ("class".equals(propertyName)) {
                continue;
            }
            if (ignorePropertiesList.contains(propertyName)) {
                continue;
            }
            Object value = sourceWrapper.getPropertyValue(propertyName);
            if (ignoreNullValue && null == value) {
                continue;
            }
            try {
                targetWrapper.setPropertyValue(propertyName, value);
            } catch (BeansException e) {
                //ignore not writable.
            }
        }
    }

    /**
     * copy properties from this vo to a new Bean
     *
     * @return Bean
     */
    public T bean() {
        try {
            T bean = clazz.newInstance();
            copyProperties(this, bean, true);
            return bean;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * copy properties from this vo to target Bean, skip null values
     *
     * @param t target Bean
     */
    public void bean(T t) {
        copyProperties(this, t, false);
    }
}
