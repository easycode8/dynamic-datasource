package com.easycode8.datasource.dynamic.core.transaction.annotation;

import com.easycode8.datasource.dynamic.core.DynamicDataSource;
import com.easycode8.datasource.dynamic.core.aop.DynamicTransactionAspect;
import com.easycode8.datasource.dynamic.core.transaction.AbstractDataSourceTransactionManager;
import com.easycode8.datasource.dynamic.core.transaction.DynamicDataSourceTransactionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DynamicTransactionManagementConfiguration {

    @Bean
    public DynamicDataSourceTransactionManager dynamicDataSourceTransactionManager(DynamicDataSource dataSource) {
        return new DynamicDataSourceTransactionManager(dataSource);
    }

    @Bean
    public DynamicTransactionAspect dynamicTransactionAspect(AbstractDataSourceTransactionManager transactionManager) {
        return new DynamicTransactionAspect(transactionManager);
    }
}
