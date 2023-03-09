package com.easycode8.datasource.dynamic.web.autoconfigure;

import com.easycode8.datasource.dynamic.core.DynamicDataSourceManager;
import com.easycode8.datasource.dynamic.web.LiquiBaseSupportController;
import com.easycode8.datasource.dynamic.web.DynamicDataSourceController;
import com.easycode8.datasource.dynamic.web.filter.SecurityBasicAuthFilter;
import liquibase.integration.spring.SpringLiquibase;
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

    @Bean
    @ConditionalOnBean(DynamicDataSourceManager.class)
    public DynamicDataSourceController dynamicDataSourceController() {
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
