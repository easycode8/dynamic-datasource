package com.easycode8.datasource.dynamic.core.transaction;

import org.springframework.core.NamedThreadLocal;
import org.springframework.transaction.support.ResourceHolder;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

public class TransactionSynchronizationManager {
    private static final ThreadLocal<Map<Object, Object>> resources = new NamedThreadLocal<>("Transactional resources");
    private static final ThreadLocal<Boolean> actualTransactionActive =
            new NamedThreadLocal<>("Actual transaction active");

    public static void bindResource(Object key, Object value) throws IllegalStateException {
        // TODO使用真实的Key
//        Object actualKey = TransactionSynchronizationUtils.unwrapResourceIfNecessary(key);


        Map<Object, Object> map = resources.get();
        // set ThreadLocal Map if none found
        if (map == null) {
            map = new HashMap<>();
            resources.set(map);
        }
        Assert.isNull(map.get(key), "禁止覆盖线程资源");
        map.put(key, value);
    }

    public static Object unbindResource(Object key) throws IllegalStateException {
        Map<Object, Object> map = resources.get();

        Object value = map.remove(key);
        // Transparently suppress a ResourceHolder that was marked as void...
        if (value instanceof ResourceHolder && ((ResourceHolder) value).isVoid()) {
            value = null;
        }
        return value;
    }

    public static Object getResource(Object key) {
        Map<Object, Object> map = resources.get();
        if (map == null) {
            return null;
        }
        return map.get(key);
    }

    public static boolean isActualTransactionActive() {
        return (actualTransactionActive.get() != null);
    }

    public static void clear() {

        actualTransactionActive.remove();
    }

    public static void setActualTransactionActive(boolean active) {
        actualTransactionActive.set(active ? Boolean.TRUE : null);
    }

    public static boolean isResourceReady() {
        return (resources.get() != null);
    }
}
