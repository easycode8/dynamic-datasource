package com.easycode8.datasource.dynamic.web.autoconfigure;

import com.easycode8.datasource.dynamic.core.DynamicDataSourceManager;
import com.easycode8.datasource.dynamic.web.LiquiBaseSupportController;
import com.easycode8.datasource.dynamic.web.DynamicDataSourceController;
import com.easycode8.datasource.dynamic.web.filter.SecurityBasicAuthFilter;
import liquibase.integration.spring.SpringLiquibase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureAfter(LiquibaseAutoConfiguration.class)
@ConditionalOnProperty(value = "spring.datasource.dynamic.web.enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(DynamicDataSourceWebProperties.class)
public class DynamicDataSourceWebAutoConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicDataSourceWebAutoConfiguration.class);


    @Bean
    @ConditionalOnBean(DynamicDataSourceManager.class)
    public DynamicDataSourceController dynamicDataSourceController() {
        LOGGER.info("[dynamic-datasource] 动态数据源-web管理页面开启:dynamic-datasource.html");
        return new DynamicDataSourceController();
    }

    @Bean
    public SecurityBasicAuthFilter securityBasicAuthFilter(DynamicDataSourceWebProperties webProperties) {
        return new SecurityBasicAuthFilter(webProperties.getEnableBasicAuth(), webProperties.getUsername(), webProperties.getPassword());
    }


    @Configuration
    public static class LiquiBaseSupportConfiguration {

        @ConditionalOnBean(SpringLiquibase.class)
        @Bean
        public LiquiBaseSupportController liquiBaseSupportController() {
            return new LiquiBaseSupportController();
        }
    }

}
