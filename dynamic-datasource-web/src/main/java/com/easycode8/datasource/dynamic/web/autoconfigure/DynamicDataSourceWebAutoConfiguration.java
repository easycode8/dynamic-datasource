package com.easycode8.datasource.dynamic.web.autoconfigure;

import com.easycode8.datasource.dynamic.core.DynamicDataSourceManager;
import com.easycode8.datasource.dynamic.web.LiquiBaseSupportController;
import com.easycode8.datasource.dynamic.web.DynamicDataSourceController;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnBean(DynamicDataSourceManager.class)
@AutoConfigureAfter(LiquibaseAutoConfiguration.class)
public class DynamicDataSourceWebAutoConfiguration {

    @Bean
    public DynamicDataSourceController dynamicDataSourceController() {
        return new DynamicDataSourceController();
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
