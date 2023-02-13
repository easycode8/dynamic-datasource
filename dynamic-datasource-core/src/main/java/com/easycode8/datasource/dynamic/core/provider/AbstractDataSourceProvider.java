package com.easycode8.datasource.dynamic.core.provider;

import com.easycode8.datasource.dynamic.core.DataSourceInfo;
import com.easycode8.datasource.dynamic.core.creator.DataSourceCreator;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractDataSourceProvider implements DataSourceProvider{
    private Map<String, DataSourceCreator> dataSourceCreatorMap = new HashMap<>();

    public AbstractDataSourceProvider(List<DataSourceCreator> dataSourceCreatorList) {

        dataSourceCreatorList.stream().forEach(item -> this.dataSourceCreatorMap.put(item.className(), item));
    }

    @Override
    public Map<String, DataSource> loadDataSources() {
        Map<String, DataSource> dataSourceMap = new HashMap<>();
        for (DataSourceInfo info : loadDataSourceInfo()) {
            DataSourceCreator creator = dataSourceCreatorMap.get(info.getType());
            if (creator == null) {
                throw new IllegalStateException("cannot find creator for:  " + info.getType());
            }
            dataSourceMap.put(info.getKey(), creator.createDataSource(info));
        }
        return dataSourceMap;
    }

    public abstract List<DataSourceInfo> loadDataSourceInfo();
}
