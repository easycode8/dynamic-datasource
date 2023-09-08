package com.easycode8.datasource.dynamic.core;

import java.sql.Connection;

public class DefaultDynamicConnectionProxyFactory implements DynamicConnectionProxyFactory {

    @Override
    public DynamicConnectionProxy createProxy(Connection connection, String owner) {
        return new DynamicConnectionProxy(connection, owner);
    }

}
