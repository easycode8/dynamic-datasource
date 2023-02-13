package com.easycode8.datasource.dynamic.core.transaction;


import com.easycode8.datasource.dynamic.core.DynamicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.lang.Nullable;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.sql.SQLException;


public class DynamicDataSourceTransactionManager extends AbstractDataSourceTransactionManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicDataSourceTransactionManager.class);


    private final DynamicDataSource dataSource;

    public DynamicDataSourceTransactionManager(DynamicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public TransactionInfo doGetTransaction() {

        TransactionInfo txInfo = new TransactionInfo();
        ConnectionHolder conHolder = (ConnectionHolder) TransactionSynchronizationManager.getResource(obtainDataSource());
        txInfo.setConnectionHolder(conHolder, false);

        return txInfo;
    }

    @Override
    public void doCommit(TransactionInfo txInfo) {

        txInfo.commit();
        txInfo.setStatus(0);

    }

    @Override
    public void doRollback(TransactionInfo txInfo) {
        txInfo.rollback();
        txInfo.setStatus(1);

    }

    @Nullable
    public DataSource getDataSource() {
        return this.dataSource;
    }

    @Override
    public void doCleanupAfterCompletion(TransactionInfo txInfo) {
        // Remove the connection holder from the thread, if exposed.
        if (txInfo.isNewConnectionHolder()) {
            LOGGER.debug("Remove the connection holder from the thread, transaction:[{}]", txInfo.getName());
            TransactionSynchronizationManager.unbindResource(this.obtainDataSource());
        }

        // Reset connection. (重置connectionHolder中数据库连接特殊设置( 是否自动提交，事务隔离级别 是否只读))
        txInfo.getConnectionHolder().getAllConnection().forEach(con -> {
            ConnectionProxy connectionProxy = (ConnectionProxy) con;
            try {
                connectionProxy.setAutoCommit(true);
            } catch (SQLException ex) {
                LOGGER.debug("Could not reset JDBC Connection after transaction", ex);
            }

            DataSourceUtils.resetConnectionAfterTransaction(connectionProxy, TransactionDefinition.ISOLATION_DEFAULT, false);

            if (txInfo.isNewConnectionHolder()) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Releasing JDBC Connection [" + connectionProxy + "] after transaction");
                }
                DataSourceUtils.releaseConnection(connectionProxy, this.dataSource);
            }
        });


        // 清理配置 connectionHolder
        txInfo.getConnectionHolder().clear();
    }

    @Override
    protected void doBegin(Object transaction, TransactionDefinition definition) throws TransactionException {
        TransactionInfo txInfo = (TransactionInfo) transaction;
        try {
            // 判断事务对象是否持有holder,并且被事务同步
            if (!txInfo.hasConnectionHolder() || txInfo.getConnectionHolder().isSynchronizedWithTransaction()) {
                ConnectionHolder connectionHolder = new ConnectionHolder();

                txInfo.setConnectionHolder(connectionHolder, true);
            }

            txInfo.getConnectionHolder().setSynchronizedWithTransaction(true);//ConnectionHolder与txInfo关联上,才能标记被事务同步

        } catch (Throwable ex) {

        } finally {

        }
        // TODO spring:设置连接(是否只读,连接的数据库事务隔离级别), 这些逻辑只能放在DynamicDatasource.getConnection() 真正获取连接时候初始化


        //TODO spring源码中是设置了conn是否是只读后(prepareTransactionalConnection),holder中的connection完全准备就绪了,才把连接holder标记为事务活跃
        // 多数据源事务因为事务开启在前的时候,还没触发切换,实际上还没真正获取切换数据源,并且执行获取连接的动作。
        // 因此这里connectionHolder与txInfo绑定了,我们就立马设置holder为事务活跃
        //prepareTransactionalConnection(con, definition);
        txInfo.getConnectionHolder().setTransactionActive(true);

        // 保证当前线程持有动态线程池活跃的connectionHolder
        // Bind the connection holder to the thread.
        if (txInfo.isNewConnectionHolder()) {
            LOGGER.info("Bind the connection holder to the thread. key:{}", obtainDataSource());
            TransactionSynchronizationManager.bindResource(obtainDataSource(), txInfo.getConnectionHolder());
        }
    }

    @Override
    protected boolean isExistingTransaction(Object transaction) {
        TransactionInfo txInfo = (TransactionInfo) transaction;
        return (txInfo.hasConnectionHolder() && txInfo.getConnectionHolder().isTransactionActive());
    }

    protected DataSource obtainDataSource() {
        DataSource dataSource = this.getDataSource();
        Assert.state(dataSource != null, "No DataSource set");
        return dataSource;
    }
}
