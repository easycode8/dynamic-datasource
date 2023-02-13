package com.easycode8.datasource.dynamic.core.transaction;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class ConnectionHolder {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionHolder.class);

    private boolean synchronizedWithTransaction = false;
    private ConcurrentMap<String, Connection> connectionHolder = new ConcurrentHashMap<>();

    private boolean transactionActive = false;

    public void putConnection(String datasourceType, Connection connection) {
        LOGGER.debug("Receive Connection:{}", connection);
        this.connectionHolder.put(datasourceType, connection);
    }

    public Connection getConnection(String datasourceType) {
        return this.connectionHolder.get(datasourceType);
    }

    public boolean isSynchronizedWithTransaction() {
        return synchronizedWithTransaction;
    }

    public void setSynchronizedWithTransaction(boolean synchronizedWithTransaction) {
        this.synchronizedWithTransaction = synchronizedWithTransaction;
    }

    public void clear() {
        this.synchronizedWithTransaction = false;
    }

    public List<Connection> getAllConnection() {
        return connectionHolder.entrySet().stream().map(item -> item.getValue()).collect(Collectors.toList());
    }

    public boolean contain(ConnectionProxy con) {
        return connectionHolder.entrySet().stream().anyMatch(item -> item.equals(con));
    }

    /**
     * 判断holder是否代表活跃的,jdbc管理的事务
     * @return
     */
    protected boolean isTransactionActive() {
        return this.transactionActive;
    }

    /**
     * 设置holder是否代表活跃的,jdbc管理的事务
     * @param transactionActive
     */
    protected void setTransactionActive(boolean transactionActive) {
        this.transactionActive = transactionActive;
    }
}