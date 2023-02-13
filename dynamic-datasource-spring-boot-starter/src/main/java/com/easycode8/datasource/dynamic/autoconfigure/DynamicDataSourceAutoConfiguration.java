package com.easycode8.datasource.dynamic.autoconfigure;

import com.easycode8.datasource.dynamic.core.annotation.EnableDynamicDataSource;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureBefore(value = DataSourceAutoConfiguration.class, name = "com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure")
public class DynamicDataSourceAutoConfiguration {


    @Configuration
    @EnableDynamicDataSource
    @ConditionalOnProperty(name = "spring.datasource.dynamic.enabled", havingValue = "true", matchIfMissing = true)
    public static class EnableDynamicDataSourceConfiguration {

    }

}
