package com.easycode8.datasource.dynamic.core;

import java.sql.Connection;

/**
 * 动态数据源连接工厂接口
 */
public interface DynamicConnectionProxyFactory {

    /**
     * 包装实际的连接转化未动态数据源的连接
     * @param connection 连接池使用的数据库连接
     * @param owner 连接所属的数据源
     * @return
     */
    DynamicConnectionProxy createProxy(Connection connection, String owner);

}
