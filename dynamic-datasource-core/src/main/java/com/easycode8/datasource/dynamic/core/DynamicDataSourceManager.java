package com.easycode8.datasource.dynamic.core;


import com.easycode8.datasource.dynamic.core.creator.DataSourceCreator;
import com.easycode8.datasource.dynamic.core.util.SpringReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 动态数据源管理器
 * <li>动态数据源的导入初始</li>
 * <li>服务数据源的增删改查</li>
 *
 */
public class DynamicDataSourceManager implements InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicDataSourceManager.class);
    private final DynamicDataSource dynamicDataSource;

    private Map<String, DataSourceCreator> dataSourceCreatorMap = new HashMap<>();
    private final DynamicDataSourceProperties dataSourceProperties;

    public DynamicDataSourceManager(DynamicDataSource dynamicDataSource, List<DataSourceCreator> dataSourceCreatorList, DynamicDataSourceProperties dataSourceProperties) {
        this.dynamicDataSource = dynamicDataSource;
        this.dataSourceProperties = dataSourceProperties;
        dataSourceCreatorList.stream().forEach(item -> this.dataSourceCreatorMap.put(item.className(), item));
    }

    public void removeDynamicDataSource(String key) {
        dynamicDataSource.remove(key);
    }

    public void addDynamicDataSource(DataSourceInfo dataSourceInfo) {

        // 测试数据源是否正常连接
        DataSourceCreator creator = dataSourceCreatorMap.get(dataSourceInfo.getType());
        DataSource dataSource = creator.createDataSource(dataSourceInfo);
        this.checkDataSource(dataSource);
        dynamicDataSource.add(dataSourceInfo.getKey(), dataSource);
    }

    public List<DataSourceInfo> listAllDataSourceInfo() {
        return dynamicDataSource.getDynamicDataSourceMap().entrySet().stream().map(item -> {
            String url = (String) SpringReflectionUtils.invokeMethod(item.getValue(), item.getValue().getClass(), "getJdbcUrl", "getUrl");
            String username = (String) SpringReflectionUtils.invokeMethod(item.getValue(), item.getValue().getClass(), "getUsername");
            String password = (String) SpringReflectionUtils.invokeMethod(item.getValue(), item.getValue().getClass(), "getPassword");
            String driverClassName = (String) SpringReflectionUtils.invokeMethod(item.getValue(), item.getValue().getClass(), "getDriverClassName");;
            return DataSourceInfo.builder()
                    .key(item.getKey())
                    .jdbcUrl(url)
                    .username(username)
                    .password(password)
                    .driverClassName(driverClassName)
                    .build();

        }).collect(Collectors.toList());
    }

    public void checkAllDataSource(boolean forceCheck) {
        for (Map.Entry<String, DataSource> item : dynamicDataSource.getDynamicDataSourceMap().entrySet()) {
            DataSource dataSource = item.getValue();
            try {
                this.checkDataSource(dataSource);
                LOGGER.info("dynamic datasource[{}] test jdbc connection is success!", item.getKey());
            } catch (Exception ex) {
                if (forceCheck) {
                    throw new IllegalStateException("dynamic datasource[" + item.getKey() + "] CannotGetJdbcConnectionException:" + ex.getMessage(), ex);
                }
                LOGGER.warn("dynamic datasource[{}] test jdbc connection is failure, cause by:{}", item.getKey(), ex.getMessage());
            }

        }
    }


    @Override
    public void afterPropertiesSet() {
        if (dynamicDataSource == null) {
            throw new IllegalStateException("dynamicDataSource not set");
        }
        this.checkAllDataSource(dataSourceProperties.getCheck());
    }

    private void checkDataSource(DataSource dataSource) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        // 不同数据库不通用
        jdbcTemplate.execute("select 1 from dual");
    }


}
