package com.easycode8.datasource.dynamic.autoconfigure;

import com.easycode8.datasource.dynamic.core.DynamicDataSource;
import com.easycode8.datasource.dynamic.core.transaction.annotation.EnableDynamicTransactionManagement;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureAfter({DynamicDataSourceAutoConfiguration.class})
public class DynamicTransactionAutoConfiguration {

    @Configuration
    @EnableDynamicTransactionManagement
    // 事务开启前提是动态切换开启
    @ConditionalOnBean(DynamicDataSource.class)
    @ConditionalOnProperty(name = "spring.datasource.dynamic.transaction.enabled", havingValue = "true", matchIfMissing = true)
    public static class EnableDynamicTransactionManagementConfiguration {

    }
}
