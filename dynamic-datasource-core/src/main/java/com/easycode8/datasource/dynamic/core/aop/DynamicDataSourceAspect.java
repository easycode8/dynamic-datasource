package com.easycode8.datasource.dynamic.core.aop;


import com.easycode8.datasource.dynamic.core.DynamicDataSourceHolder;
import com.easycode8.datasource.dynamic.core.DynamicDataSourceProperties;
import com.easycode8.datasource.dynamic.core.annotation.DynamicSource;
import com.easycode8.datasource.dynamic.core.util.SpringSpelUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.text.MessageFormat;

@Aspect
@Order(1) // 这个切面需要比spring的事务管理器的切面先执行(默认的为Integer.MAX_VALUE,值越小切面越先执行), 否则会导致使用默认的数据源
public class DynamicDataSourceAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicDataSourceAspect.class);


    private DynamicDataSourceProperties dataSourceProperties;

    public DynamicDataSourceAspect(DynamicDataSourceProperties dataSourceProperties) {
        this.dataSourceProperties = dataSourceProperties;
    }

    @Before("@annotation(dynamicSource)")
    public void changeDataSource(JoinPoint point, DynamicSource dynamicSource) throws Throwable {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            LOGGER.warn("已经开启spring事务,事务内切换数据源失效");
            return;
        }
        String headerKey = dataSourceProperties.getHeader();

        // HttpServletRequest 对象在项目启动后执行不同数据源切换初始化,是非web请求会报错这里兼容这个使用场景忽略报错
        HttpServletRequest request = null;
        try {
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            request = requestAttributes.getRequest();
        } catch (Exception e) {
            LOGGER.warn("get http error:" + e.getMessage());
        }

        String dataBaseType = this.parseDataSourceType(point, dynamicSource);
        if (StringUtils.isNotBlank(dynamicSource.value())) {
            // 如果解析出来数据源类型和解析前一样,说明是代码写死得数据源配置而不是运行时动态创建,那么不进行安全检查要求yaml里面一定要配置
            if (StringUtils.equals(dataBaseType, dynamicSource.value()) && !dataSourceProperties.getDatasource().keySet().contains(dataBaseType)) {
                throw new IllegalStateException(MessageFormat.format("注解数据源未定义:{0},调整或增加配置:{1}.{0}", dataBaseType, "spring.datasource.dynamic.datasource"));
            }
            LOGGER.debug("动态数据源栈--入栈切换:【{}】注解方式", dataBaseType);
        } else if (request != null && StringUtils.isNotBlank(request.getHeader(headerKey))) {
            dataBaseType = request.getHeader(headerKey);
            LOGGER.debug("动态数据源--入栈切换:【{}】请求头方式", headerKey, dataBaseType);
        }  else {
            // 注解中未定义数据源,则使用首选数据源
            dataBaseType = dataSourceProperties.getPrimary();
            LOGGER.debug("动态数据源--入栈切换:【{}】默认首选", dataBaseType);
        }

        DynamicDataSourceHolder.push(dataBaseType);
        LOGGER.debug("数据源栈:{}", DynamicDataSourceHolder.show());
    }

    @After("@annotation(dynamicSource)")
    public void restoreDataSource(JoinPoint point, DynamicSource dynamicSource) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            LOGGER.debug("已经开启spring事务切换数据源失效");
            return;
        }
        //清除数据源的配置
        LOGGER.trace("数据源栈:{}", DynamicDataSourceHolder.show());
        LOGGER.debug("动态数据源栈--出栈解绑:{}", DynamicDataSourceHolder.peek());
        DynamicDataSourceHolder.poll();

    }

    private String parseDataSourceType(JoinPoint joinPoint, DynamicSource dynamicSource) {
        String spelTemplate = dynamicSource.value();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        try {
            return SpringSpelUtils.parse(method, joinPoint.getArgs(), spelTemplate);
        } catch (Exception e) {
            // 解析报错直接忽略
        }

        return spelTemplate;
    }
}
