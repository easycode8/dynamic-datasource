package com.easycode8.datasource.dynamic.core.creator;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.pool.DruidDataSource;

import com.easycode8.datasource.dynamic.core.DataSourceInfo;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.util.List;


public class DruidDataSourceCreator extends AbstractDataSourceCreator{
    private static final Logger LOGGER = LoggerFactory.getLogger(DruidDataSourceCreator.class);
    public static final String TYPE_CLASS = "com.alibaba.druid.pool.DruidDataSource";
    private final Environment environment;

    private final List<Filter> filters;

    public DruidDataSourceCreator(Environment environment, List<Filter> filters) {
        this.environment = environment;
        this.filters = filters;
    }


    @Override
    public DataSource doCreateDataSource(DataSourceInfo dataSourceInfo) throws Exception {
        String prefix = "spring.datasource.dynamic.datasource." + dataSourceInfo.getKey()+ ".druid";
        DruidDataSource dataSource = Binder.get(environment).bind(prefix, DruidDataSource.class).orElse(null);
        DruidDataSource defaultDruid = Binder.get(environment).bindOrCreate("spring.datasource.druid", DruidDataSource.class);
        if (dataSource == null) {
            LOGGER.info("【easy-model】动态数据源--{}未指定druid配置:{},使用默认druid starter配置", dataSourceInfo.getKey(), prefix);
            dataSource = defaultDruid;
        } else {
            // 使用单个数据源下个性化配置覆盖默认的
            BeanUtils.copyProperties(dataSource, Binder.get(environment).bindOrCreate("spring.datasource.druid", DruidDataSource.class));
        }
        dataSource.setUrl(dataSourceInfo.getUrl());
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
