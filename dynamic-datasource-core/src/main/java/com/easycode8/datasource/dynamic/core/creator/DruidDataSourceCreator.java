package com.easycode8.datasource.dynamic.core.creator;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.pool.DruidDataSource;

import com.easycode8.datasource.dynamic.core.DataSourceInfo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.util.List;


public class DruidDataSourceCreator extends AbstractDataSourceCreator{
    public static final String TYPE_CLASS = "com.alibaba.druid.pool.DruidDataSource";
    private final Environment environment;

    private final List<Filter> filters;

    public DruidDataSourceCreator(Environment environment, List<Filter> filters) {
        this.environment = environment;
        this.filters = filters;
    }


    @Override
    public DataSource doCreateDataSource(DataSourceInfo dataSourceInfo) throws Exception {

        DruidDataSource dataSource = Binder.get(environment).bindOrCreate("spring.datasource.druid", DruidDataSource.class);
        dataSource.setUrl(dataSourceInfo.getJdbcUrl());
        dataSource.setUsername(dataSourceInfo.getUsername());
        dataSource.setPassword(dataSourceInfo.getPassword());
        dataSource.setDriverClassName(dataSourceInfo.getDriverClassName());
//        dataSource.setValidationQuery("select 1");
        if (CollectionUtils.isNotEmpty(filters)) {
            dataSource.setProxyFilters(filters);
        }
        dataSource.init();
        return dataSource;
    }

    @Override
    public String className() {
        return TYPE_CLASS;
    }
}
