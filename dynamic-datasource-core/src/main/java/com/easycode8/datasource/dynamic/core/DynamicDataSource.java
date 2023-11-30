package com.easycode8.datasource.dynamic.core;

import com.easycode8.datasource.dynamic.core.exception.DynamicDataSourceNotFoundException;
import com.easycode8.datasource.dynamic.core.provider.DataSourceProvider;
import com.easycode8.datasource.dynamic.core.transaction.ConnectionHolder;
import com.easycode8.datasource.dynamic.core.transaction.TransactionSynchronizationManager;
import com.easycode8.datasource.dynamic.core.util.JdbcUrlParserUtils;
import com.easycode8.datasource.dynamic.core.util.SpringReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DynamicDataSource extends AbstractRoutingDataSource implements DisposableBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicDataSource.class);
    private ConcurrentHashMap<String, DataSource> dynamicDataSourceMap = new ConcurrentHashMap<>();
    private final DataSourceProperties dataSourceProperties;
    private final DynamicDataSourceProperties dynamicDataSourceProperties;
    private final List<DataSourceProvider> dataSourceProviders;
    private final DynamicConnectionProxyFactory dynamicConnectionProxyFactory;



    public DynamicDataSource(DataSourceProperties dataSourceProperties, DynamicDataSourceProperties dynamicDataSourceProperties, List<DataSourceProvider> dataSourceProviders, DynamicConnectionProxyFactory dynamicConnectionProxyFactory) {
        this.dataSourceProperties = dataSourceProperties;
        this.dynamicDataSourceProperties = dynamicDataSourceProperties;
        this.dataSourceProviders = dataSourceProviders;
        this.dynamicConnectionProxyFactory = dynamicConnectionProxyFactory;
    }

    @Override
    protected Object determineCurrentLookupKey() {
        String lookupKey = DynamicDataSourceHolder.peek();
        return StringUtils.isEmpty(lookupKey)? dynamicDataSourceProperties.getPrimary() : lookupKey;
    }


    @Override
    protected DataSource determineTargetDataSource() {
        // 首先在动态多数据源选择
        String key = DynamicDataSourceHolder.peek();
        if (dynamicDataSourceMap.get(key) != null) {
            return dynamicDataSourceMap.get(DynamicDataSourceHolder.peek());
        }
        // key不为空情况下,严格使用指定数据源将报错
        if (!StringUtils.isEmpty(key) && dynamicDataSourceProperties.getStrict()) {
            throw new DynamicDataSourceNotFoundException("未匹配到数据源:" + key);
        }
        // 没有找到则在静态多数据源寻找
        return super.determineTargetDataSource();
    }

    @Override
    public Connection getConnection() throws SQLException {
        String lookupKey = this.determineCurrentLookupKey().toString();
        Connection connection = null;
        try {

            if (!TransactionSynchronizationManager.isActualTransactionActive()) {
                connection = super.getConnection();
                connection = dynamicConnectionProxyFactory.createProxy(connection, lookupKey);
                LOGGER.info("[dynamic-datasource] Acquired {} for Dynamic JDBC no transaction", connection);
                return connection;
            }
            // 根据动态数据源获取当前线程中活跃的连接状态
            ConnectionHolder connectionHolder = (ConnectionHolder) TransactionSynchronizationManager.getResource(this);
            Assert.notNull(connectionHolder, "事务中获取根据动态数据源获取连接connectionHolder不能为空");
            connection = connectionHolder.getConnection(lookupKey);
            if (connection != null) {
                return connection;
            }

            // 获取当前数据源的连接设置手动提交
            connection = dynamicConnectionProxyFactory.createProxy(connection, lookupKey);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("[dynamic-datasource] Acquired ConnectionProxy [" + connection + "] for Dynamic JDBC transaction");
            }
            connection.setAutoCommit(false);
            connectionHolder.putConnection(lookupKey, connection);

            return connection;
        } catch (Exception e) {
            DataSourceUtils.releaseConnection(connection,this);
            throw e;
        }


    }

    @Override
    public void afterPropertiesSet() {
        loadAllDataSource();
        chooseDefaultDataSource();
        super.afterPropertiesSet();
    }

    @Override
    public void destroy() throws Exception {
        LOGGER.info("[dynamic-datasource] dynamic datasource start closing ....");
        for (Map.Entry<String, DataSource> item : dynamicDataSourceMap.entrySet()) {
            this.closeDataSource(item.getKey(), item.getValue());
        }
        LOGGER.info("[dynamic-datasource] dynamic datasource all closed success");
    }


    protected synchronized void add(String dataSourceKey, DataSource dataSource) {
        if (dynamicDataSourceMap.containsKey(dataSourceKey)) {
            throw new IllegalStateException("[dynamic-datasource] dynamic datasource already exist key:" + dataSourceKey);
        }
        dynamicDataSourceMap.put(dataSourceKey, dataSource);
    }


    protected synchronized void remove(String key) {
        if (dynamicDataSourceProperties.getPrimary().equals(key)) {
            throw new IllegalStateException("[dynamic-datasource] primary dynamic datasource cannot remove " + key);
        }
        DataSource dataSource = this.dynamicDataSourceMap.remove(key);
        if (dataSource != null) {
            this.closeDataSource(key, dataSource);
        }
    }


    protected ConcurrentHashMap<String, DataSource> getDynamicDataSourceMap() {
        return dynamicDataSourceMap;
    }

    /**
     * 获取当前动态数据源的数据库类型
     * @return
     */
    public String getCurrentDatabaseType() {
        String jdbcUrl = (String) SpringReflectionUtils.invokeMethod(determineTargetDataSource(), determineTargetDataSource().getClass(), "getJdbcUrl", "getUrl");
        return JdbcUrlParserUtils.extractDatabaseType(jdbcUrl);
    }

    private void closeDataSource(String dsName, DataSource dataSource) {
        LOGGER.info("[dynamic-datasource] close dynamic datasource :{}", dsName);
        Method closeMethod = ReflectionUtils.findMethod(dataSource.getClass(), "close");
        if (closeMethod != null) {
            try {
                closeMethod.invoke(dataSource);
            } catch (Exception e) {
                LOGGER.error("[dynamic-datasource] close dynamic datasource:{}", dsName, e);
            }
        }
    }


    private void chooseDefaultDataSource() {
        DataSource primaryDataSource = dynamicDataSourceMap.get(dynamicDataSourceProperties.getPrimary());
        if (primaryDataSource == null) {
            throw new IllegalStateException("[dynamic-datasource] 未找到首选数据源:" + dynamicDataSourceProperties.getPrimary());
        }
        this.setDefaultTargetDataSource(primaryDataSource);
        LOGGER.info("[dynamic-datasource] 动态数据源--主源:{}", dynamicDataSourceProperties.getPrimary());
        LOGGER.info("[dynamic-datasource] 动态数据源--切换请求头:{}", dynamicDataSourceProperties.getHeader());
    }

    private void loadAllDataSource() {
        this.setTargetDataSources(new HashMap<>());
        for (DataSourceProvider provider : dataSourceProviders) {
            dynamicDataSourceMap.putAll(provider.loadDataSources());
        }
        if (CollectionUtils.isEmpty(dynamicDataSourceMap)) {
            LOGGER.warn("[dynamic-datasource] 未读取到动态数据源,使用spring原生数据源作为首选数据源");
            dynamicDataSourceMap.put(dynamicDataSourceProperties.getPrimary(), dataSourceProperties.initializeDataSourceBuilder().build());
        } else {
            if (!dynamicDataSourceMap.containsKey(dynamicDataSourceProperties.getPrimary())) {
                LOGGER.warn("[dynamic-datasource] 未设置动态数据源主源,使用spring原生数据源作为首选数据源");
                dynamicDataSourceMap.put(dynamicDataSourceProperties.getPrimary(), dataSourceProperties.initializeDataSourceBuilder().build());
            }
        }
        LOGGER.info("[dynamic-datasource] 动态数据源--加载成功:{}", String.join(",", dynamicDataSourceMap.keySet()));
    }


}
