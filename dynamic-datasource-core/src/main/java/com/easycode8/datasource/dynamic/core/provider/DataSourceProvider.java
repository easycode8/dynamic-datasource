package com.easycode8.datasource.dynamic.core.provider;

import javax.sql.DataSource;
import java.util.Map;

public interface DataSourceProvider {
    /**
     * 加载所有数据源
     *
     * @return 所有数据源，key为数据源名称
     */
    Map<String, DataSource> loadDataSources();
}
