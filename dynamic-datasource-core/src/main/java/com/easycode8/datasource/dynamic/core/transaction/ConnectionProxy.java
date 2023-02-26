package com.easycode8.datasource.dynamic.core.transaction;

import com.easycode8.datasource.dynamic.core.DynamicConnectionProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 多数据源事务中使用的连接代理,注释默认的行为,防止非sping事务场景下,
 * mybatis自动提交处理事务不受统一控制
 */
public class ConnectionProxy extends DynamicConnectionProxy {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionProxy.class);

    public ConnectionProxy(Connection connection, String owner) {
        super(connection, owner);
    }


    public void doCommit() throws SQLException {
        super.commit();
    }


    public void doRollback() throws SQLException {
        super.rollback();
    }


    public void doClose() throws SQLException {
        super.close();
    }

    @Override
    public void commit() throws SQLException {
        // connection.commit();
        LOGGER.debug("ignore commit");
    }

    @Override
    public void rollback() throws SQLException {
        // connection.rollback();
        LOGGER.debug("ignore rollback");
    }

    @Override
    public void close() throws SQLException {
        // connection.close();
        LOGGER.debug("ignore close");
    }


}