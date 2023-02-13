package com.easycode8.datasource.dynamic.core.creator;


import com.easycode8.datasource.dynamic.core.DataSourceInfo;
import org.springframework.util.ClassUtils;

import javax.sql.DataSource;

public abstract class AbstractDataSourceCreator implements DataSourceCreator {


    @Override
    public DataSource createDataSource(DataSourceInfo dataSourceInfo) {
        DataSource dataSource;
        try {
            dataSource = this.doCreateDataSource(dataSourceInfo);
        } catch (Exception e) {
            throw new IllegalArgumentException("create dynamic DataSource failure:" +  e.getMessage());
        }
        return dataSource;
    }

    protected Class<? extends DataSource> getType() {
        try {
            return (Class<? extends DataSource>) ClassUtils.forName(className(), null);
        }
        catch (Exception ex) {
            // Swallow and continue
        }
        return null;
    }



    public abstract DataSource doCreateDataSource(DataSourceInfo dataSourceInfo) throws Exception;

}
