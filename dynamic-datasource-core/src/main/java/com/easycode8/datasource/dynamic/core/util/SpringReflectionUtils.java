package com.easycode8.datasource.dynamic.core.util;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

public class SpringReflectionUtils {

    /**
     * 反射调用方法,如果没有找到尝试下一个方法
     * @param target
     * @param clazz
     * @param methodNames
     * @return
     */
    public static Object invokeMethod(Object target, Class<?> clazz, String... methodNames) {
        for (String methodName : methodNames) {
            Method method = ReflectionUtils.findMethod(clazz, methodName);
            if (method != null) {
                return ReflectionUtils.invokeMethod(method, target);
            }
        }
        return null;
    }
}
