package com.easycode8.datasource.dynamic.core.creator;



import com.easycode8.datasource.dynamic.core.DataSourceInfo;

import javax.sql.DataSource;

public interface DataSourceCreator {

    /**
     * 通过属性创建数据源
     *
     * @param dataSourceInfo 数据源属性
     * @return 被创建的数据源
     */
    DataSource createDataSource(DataSourceInfo dataSourceInfo);

    /**
     * 源类型名称
     * @return
     */
    String className();
}
