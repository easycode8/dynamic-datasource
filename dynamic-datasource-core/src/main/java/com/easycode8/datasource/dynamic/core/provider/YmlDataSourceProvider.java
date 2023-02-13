package com.easycode8.datasource.dynamic.core.provider;

import com.easycode8.datasource.dynamic.core.DataSourceInfo;
import com.easycode8.datasource.dynamic.core.DynamicDataSourceProperties;
import com.easycode8.datasource.dynamic.core.creator.DataSourceCreator;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class YmlDataSourceProvider extends AbstractDataSourceProvider {
    private DynamicDataSourceProperties dataSourceProperties;

    public YmlDataSourceProvider(List<DataSourceCreator> dataSourceCreator, DynamicDataSourceProperties dataSourceProperties) {
        super(dataSourceCreator);
        this.dataSourceProperties = dataSourceProperties;
    }

    @Override
    public List<DataSourceInfo> loadDataSourceInfo() {
        Map<String, DataSourceInfo> datasource = dataSourceProperties.getDatasource();
        return datasource.entrySet().stream().map(item -> {
            item.getValue().setKey(item.getKey());
            return item.getValue();
        }).collect(Collectors.toList());

    }

}
