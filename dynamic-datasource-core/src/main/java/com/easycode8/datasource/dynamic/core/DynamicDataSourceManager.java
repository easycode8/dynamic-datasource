package com.easycode8.datasource.dynamic.core;


import java.util.List;

/**
 * 动态数据源管理器
 * <li>动态数据源的导入初始</li>
 * <li>服务数据源的增删改查</li>
 */
public interface DynamicDataSourceManager {


    void removeDynamicDataSource(String key);

    void addDynamicDataSource(DataSourceInfo dataSourceInfo);

    List<DataSourceInfo> listAllDataSourceInfo();


}
