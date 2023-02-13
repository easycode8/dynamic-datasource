package com.easycode8.datasource.dynamic.core.transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;

public abstract class AbstractDataSourceTransactionManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDataSourceTransactionManager.class);
    public static final ThreadLocal<TransactionInfo> TRANSACTION_INFO_HOLDER = new ThreadLocal<>();
    public TransactionInfo createTransaction(String methodIdentification) {
        //String methodIdentification = ClassUtils.getQualifiedMethodName(method, targetClass);
        TransactionInfo txInfo = getTransaction(methodIdentification);

        //prepareTransactionInfo： Prepare a TransactionInfo for the given attribute and status object.
        // 只有开启新事务才要使用栈的结构(链表实现)
        txInfo.setOld(TRANSACTION_INFO_HOLDER.get());
        TRANSACTION_INFO_HOLDER.set(txInfo);
        return txInfo;
    }

    private TransactionInfo getTransaction(String methodIdentification) {
        TransactionInfo txInfo = doGetTransaction();
        txInfo.setName(methodIdentification);

        if (isExistingTransaction(txInfo)) {
            LOGGER.info("【easy-mode】(PROPAGATION_REQUIRED)外层有事务，当前事务加入到外层事务");
            txInfo.setNewTransaction(false);
            txInfo.setNewSynchronization(false);
            prepareSynchronization(txInfo);
            return txInfo;
        }
        txInfo.setNewTransaction(true);
        txInfo.setNewSynchronization(true);
        LOGGER.debug("【easy-mode】begin dynamic transaction for[{}]", txInfo.getName());
        this.doBegin(txInfo, null);
        // 准备同步信息(设置事务信息至TransactionSynchronizationManager)
        prepareSynchronization(txInfo);
        return txInfo;
    }

    public  void completeTransactionAfterThrowing(TransactionInfo txInfo, Throwable ex) {
        // 如果是新事务才执行回滚
        if (txInfo.isNewTransaction()) {
            this.rollback(txInfo);
        }

    }

    public  void cleanupTransactionInfo(TransactionInfo txInfo) {
        // Reset the TransactionInfo ThreadLocal
        if (txInfo != null) {
            TransactionInfo old = txInfo.getOld();
            if (old == null) {
                LOGGER.debug("【easy-mode】Remove current thread TransactionInfo:{}", txInfo.getName());
            } else {
                LOGGER.debug("【easy-mode】RestoreThreadLocalStatus to {}", old.getName());
            }
            //restoreThreadLocalStatus
            TRANSACTION_INFO_HOLDER.set(old);
        }


    }

    public void cleanupAfterCompletion(TransactionInfo txInfo) {
        // 清理事务同步器的中的信息
        if (txInfo.isNewSynchronization()) {
            TransactionSynchronizationManager.clear();
        }

        // 如果是新事务才执行清理
        if (txInfo.isNewTransaction()) {
            doCleanupAfterCompletion(txInfo);
        }

    }

    /**
     * @see
     * @param txInfo
     */
    public  void commitTransactionAfterReturning(TransactionInfo txInfo) {
        // TODO 健壮性优化:事务是否已经完成
        // TODO 优化事务是否设置全局，回滚


        if (txInfo != null) {
            LOGGER.debug("Completing Dynamic Transaction for [" + txInfo.getName() + "]");
            this.commit(txInfo);
        }

    }

    public final void commit(TransactionInfo txInfo) {
        try {

            if (txInfo.isNewTransaction()) {
                doCommit(txInfo);
            }


            txInfo.setStatus(0);
        } catch (Exception ex) {
            txInfo.setStatus(2);
            throw ex;
        } finally {
            cleanupAfterCompletion(txInfo);
        }
    }

    public final void rollback(TransactionInfo txInfo) {
        try {
            if (txInfo.isNewTransaction()) {
                doRollback(txInfo);
            }
            txInfo.setStatus(1);
        } catch (Exception ex) {
            txInfo.setStatus(2);
            throw ex;
        } finally {

            cleanupAfterCompletion(txInfo);
        }
    }

    protected void prepareSynchronization(TransactionInfo txInfo) {
        // prepareSynchronization
        if (txInfo.isNewSynchronization()) {
            TransactionSynchronizationManager.setActualTransactionActive(true);
        }
    }


    protected abstract TransactionInfo doGetTransaction();
    protected abstract void doCommit(TransactionInfo txInfo);
    protected abstract void doRollback(TransactionInfo txInfo);

    /**
     * 清理事务中绑定的资源(connectionHolder及holder持有的连接)
     * @param txInfo
     */
    protected abstract void doCleanupAfterCompletion(TransactionInfo txInfo);
    protected abstract void doBegin(Object transaction, TransactionDefinition definition)
            throws TransactionException;
    protected abstract boolean isExistingTransaction(Object transaction);


}
