package com.easycode8.datasource.dynamic.core;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.NamedThreadLocal;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * 当前线程的多数据源持有对象
 */
public class DynamicDataSourceHolder {

    /**
     * 为什么不能用String 而要使用队列(这里作为栈实现)来存储
     * 因为每次一个方法做多数据源aop增强时候。都会在进入方法前为当前线程绑定数据源标识。结束时候清除标识
     * 如果是A->B->C嵌套切换调用场景。只用String,当前请求线程中动态数据源标识，则进入被C的数据源覆盖, 退出则被清除,导致回到B,A时候是没有标识。只会使用默认
     * 因此必须使用栈的,先进后出。保证嵌套调用后,调用者还持有正确的数据源
     */
    private static final ThreadLocal<Deque<String>> DATA_SOURCE_DEQUE = new NamedThreadLocal<Deque<String>>("dynamic-datasource") {
        @Override
        protected Deque<String> initialValue() {
            return new ArrayDeque<>();
        }
    };

    /**
     * 入栈
     * @param type
     */
    public static void push(String type) {
        DATA_SOURCE_DEQUE.get().push(type);
    }

    /**
     * 获取栈顶元素
     * @return
     */
    public static String peek() {
        String dataDataSource = DATA_SOURCE_DEQUE.get().peek();
        return StringUtils.isEmpty(dataDataSource) ? StringUtils.EMPTY : dataDataSource;
    }


    /**
     * 移除栈顶数据源,如果是最后元素,则清空线程数据
     */
    public static void poll() {
        Deque<String> deque = DATA_SOURCE_DEQUE.get();
        deque.poll();
        if (deque.isEmpty()) {
            DATA_SOURCE_DEQUE.remove();
        }
    }

    public static String  show() {
        return DATA_SOURCE_DEQUE.get().toString();
    }
}
