package com.easycode8.datasource.dynamic.core.aop;


import com.easycode8.datasource.dynamic.core.transaction.AbstractDataSourceTransactionManager;
import com.easycode8.datasource.dynamic.core.transaction.TransactionInfo;
import com.easycode8.datasource.dynamic.core.transaction.annotation.DynamicTransactional;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.ProxyTransactionManagementConfiguration;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * @see ProxyTransactionManagementConfiguration
 */
@Aspect
@Order(0)//动态数据源事务要在切换数据源前面执行
public class DynamicTransactionAspect {



    private AbstractDataSourceTransactionManager transactionManager;

    public DynamicTransactionAspect(AbstractDataSourceTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    /**
     * @see TransactionInterceptor invoke
     * @param joinPoint
     * @param dynamicTransactional
     * @return
     * @throws Throwable
     */
    @Around("@annotation(dynamicTransactional)")
    public Object around(ProceedingJoinPoint joinPoint, DynamicTransactional dynamicTransactional) throws Throwable {

        // 创建事务
        Class<?> targetClass = (joinPoint.getTarget() != null ? AopUtils.getTargetClass(joinPoint.getTarget()) : null);
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodIdentification = ClassUtils.getQualifiedMethodName(signature.getMethod(), targetClass);
//        System.out.println("=====动态数据源事务切面--开始======" + methodIdentification);
        Assert.isTrue(!TransactionSynchronizationManager.isSynchronizationActive(), methodIdentification + "包含在spring事务中,不可使用动态数据源事务");
        TransactionInfo txInfo = transactionManager.createTransaction(methodIdentification);

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable ex) {
            // 执行异常--事务回滚
            transactionManager.completeTransactionAfterThrowing(txInfo, ex);
            throw ex;
        } finally {
            // 方法结束--清理线程事务信息(说明:无论是方法执行成功还是失败,当前事务都算执行完成,必须清理或者回到外层的事务信息)
            transactionManager.cleanupTransactionInfo(txInfo);
        }
        // 执行成功--提交事务
        transactionManager.commitTransactionAfterReturning(txInfo);
//        System.out.println("=====动态数据源事务切面--结束======" + txInfo.getName());
        return result;
    }
}
