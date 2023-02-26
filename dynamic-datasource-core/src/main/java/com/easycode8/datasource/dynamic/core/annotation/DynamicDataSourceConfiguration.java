package com.easycode8.datasource.dynamic.core.annotation;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.pool.DruidDataSource;
import com.easycode8.datasource.dynamic.core.DefaultDynamicDataSourceManager;
import com.easycode8.datasource.dynamic.core.provider.DataSourceProvider;
import com.easycode8.datasource.dynamic.core.DynamicDataSource;
import com.easycode8.datasource.dynamic.core.DynamicDataSourceManager;
import com.easycode8.datasource.dynamic.core.DynamicDataSourceProperties;
import com.easycode8.datasource.dynamic.core.aop.DynamicDataSourceAspect;
import com.easycode8.datasource.dynamic.core.creator.DataSourceCreator;
import com.easycode8.datasource.dynamic.core.creator.DefaultDataSourceCreator;
import com.easycode8.datasource.dynamic.core.creator.DruidDataSourceCreator;
import com.easycode8.datasource.dynamic.core.provider.YmlDataSourceProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.List;

@Configuration
@EnableConfigurationProperties(DynamicDataSourceProperties.class)
public class DynamicDataSourceConfiguration {

    @Bean(name = "dynamicDataSource")
    @ConditionalOnMissingBean
    public DynamicDataSource DataSource(DynamicDataSourceProperties dataSourceProperties, List<DataSourceProvider> dataSourceProviders) {
        DynamicDataSource dataSource = new DynamicDataSource(dataSourceProperties, dataSourceProviders);
        return dataSource;
    }

    @Bean
    @ConditionalOnMissingBean
    public DynamicDataSourceManager dynamicDataSourceManager(DynamicDataSource dynamicDataSource, List<DataSourceCreator> dataSourceCreator, DynamicDataSourceProperties dataSourceProperties) {
        return new DefaultDynamicDataSourceManager(dynamicDataSource, dataSourceCreator, dataSourceProperties);
    }

    @Bean
    public DynamicDataSourceAspect dynamicDataSourceAspect(DynamicDataSourceProperties dataSourceProperties) {
        return new DynamicDataSourceAspect(dataSourceProperties);
    }



    @Bean
    public YmlDataSourceProvider ymlDataSourceProvider(List<DataSourceCreator> creator, DynamicDataSourceProperties properties) {
        return new YmlDataSourceProvider(creator, properties);
    }

    @Bean
    public DefaultDataSourceCreator defaultDataSourceCreator() {
        return new DefaultDataSourceCreator();
    }

    @ConditionalOnClass(DruidDataSource.class)
    @Configuration
    public static class DataSourceCreatorConfiguration {

        @Bean
        public DruidDataSourceCreator druidDataSourceCreator(Environment environment, List<Filter> filters) {
            return new DruidDataSourceCreator(environment, filters);
        }
    }
}
