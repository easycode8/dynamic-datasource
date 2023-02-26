package com.easycode8.datasource.dynamic.core.creator;


import com.easycode8.datasource.dynamic.core.DataSourceInfo;
import org.springframework.boot.jdbc.DataSourceBuilder;

import javax.sql.DataSource;

public class DefaultDataSourceCreator extends AbstractDataSourceCreator{

    @Override
    public DataSource doCreateDataSource(DataSourceInfo info) {
        // !!!该数据源没有使用连接池
//        DriverManagerDataSource dataSource = new DriverManagerDataSource();
//        dataSource.setDriverClassName(info.getDriverClassName());
//        dataSource.setUrl(info.getUrl());
//        dataSource.setUsername(info.getUsername());
//        dataSource.setPassword(info.getPassword());
//        return dataSource;


        return  DataSourceBuilder.create()
                .password(info.getPassword())
                .username(info.getUsername())
                .url(info.getUrl())
                .type(this.getType())
                .build();
    }

    @Override
    public String className() {
        return "default";
    }
}
