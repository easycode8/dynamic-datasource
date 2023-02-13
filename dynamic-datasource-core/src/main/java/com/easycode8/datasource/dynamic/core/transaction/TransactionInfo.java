package com.easycode8.datasource.dynamic.core.transaction;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.transaction.TransactionSystemException;

import java.sql.Connection;
import java.sql.SQLException;

public class TransactionInfo {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionInfo.class);
    //事务名称--使用方法名
    private String name;
    //TODO 事务状态: 暂时只是标记
    private Integer status;
    //事务持有的连接对象
    private ConnectionHolder connectionHolder;
    private Boolean newConnectionHolder;

    private  boolean newTransaction;

    private  boolean newSynchronization;

    private TransactionInfo old;

    public TransactionInfo() {
        this.newConnectionHolder = true;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }



    public boolean isNewConnectionHolder() {
        return this.newConnectionHolder;
    }

    public ConnectionHolder getConnectionHolder() {
        return connectionHolder;
    }

    public void setConnectionHolder(@Nullable ConnectionHolder connectionHolder, Boolean newConnectionHolder) {
        this.connectionHolder = connectionHolder;
        this.newConnectionHolder = newConnectionHolder;
    }

    public void commit() {
        for (Connection connection : connectionHolder.getAllConnection()) {

            try {
                ConnectionProxy proxy = (ConnectionProxy) connection;
                LOGGER.debug("Committing Dynamic JDBC transaction on ConnectionProxy [" + proxy + "]");
                proxy.doCommit();
            }
            catch (SQLException ex) {
                throw new TransactionSystemException("Could not commit JDBC transaction", ex);
            }
        }
    }

    public void rollback() {
        for (Connection connection : connectionHolder.getAllConnection()) {
            try {
                ConnectionProxy proxy = (ConnectionProxy) connection;
                LOGGER.debug("Rolling back Dynamic JDBC transaction on Connection [" + proxy + "]");
                proxy.doRollback();
            }
            catch (SQLException ex) {
                throw new TransactionSystemException("Could not commit Dynamic JDBC transaction", ex);
            }
        }
    }

    public boolean hasConnectionHolder() {
        return (this.connectionHolder != null);
    }

    public TransactionInfo getOld() {
        return old;
    }

    public void setOld(TransactionInfo old) {
        this.old = old;
    }

    public boolean isNewTransaction() {
        return newTransaction;
    }

    public void setNewTransaction(boolean newTransaction) {
        this.newTransaction = newTransaction;
    }

    public boolean isNewSynchronization() {
        return newSynchronization;
    }

    public void setNewSynchronization(boolean newSynchronization) {
        this.newSynchronization = newSynchronization;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
