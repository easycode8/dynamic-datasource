package com.easycode8.datasource.dynamic.core;

import com.easycode8.datasource.dynamic.core.provider.DataSourceProvider;
import com.easycode8.datasource.dynamic.core.transaction.ConnectionHolder;
import com.easycode8.datasource.dynamic.core.transaction.ConnectionProxy;
import com.easycode8.datasource.dynamic.core.transaction.TransactionSynchronizationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
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
    private final DynamicDataSourceProperties dataSourceProperties;
    private final List<DataSourceProvider> dataSourceProviders;



    public DynamicDataSource(DynamicDataSourceProperties dataSourceProperties, List<DataSourceProvider> dataSourceProviders) {
        this.dataSourceProperties = dataSourceProperties;
        this.dataSourceProviders = dataSourceProviders;
    }

    @Override
    protected Object determineCurrentLookupKey() {
        String lookupKey = DynamicDataSourceHolder.peek();
        return StringUtils.isEmpty(lookupKey)? dataSourceProperties.getPrimary() : lookupKey;
    }


    @Override
    protected DataSource determineTargetDataSource() {
        // 首先在动态多数据源选择
        if (dynamicDataSourceMap.get(DynamicDataSourceHolder.peek()) != null) {
            return dynamicDataSourceMap.get(DynamicDataSourceHolder.peek());
        }
        // 没有找到则在静态多数据源寻找
        return super.determineTargetDataSource();
    }

    @Override
    public Connection getConnection() throws SQLException {
        // TODO 执行获取连接过程有任何报错需要finally释放连接
        String lookupKey = this.determineCurrentLookupKey().toString();
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            Connection connection = new ConnectionProxy(super.getConnection(), lookupKey);
            LOGGER.info("Create No Dynamic JDBC Transaction connection:{}", connection);
            return connection;
        }
        // 根据动态数据源获取当前线程中活跃的连接状态
        ConnectionHolder connectionHolder = (ConnectionHolder) TransactionSynchronizationManager.getResource(this);
        Assert.notNull(connectionHolder, "事务中获取根据动态数据源获取连接connectionHolder不能为空");
        Connection connection = connectionHolder.getConnection(lookupKey);
        if (connection != null) {
            return connection;
        }

        // 获取当前数据源的连接设置手动提交
        connection = new ConnectionProxy(super.getConnection(), lookupKey);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Acquired ConnectionProxy [" + connection + "] for Dynamic JDBC transaction");
        }
        connection.setAutoCommit(false);
        // ...设置连接的事务隔离级别//是否只读
        connectionHolder.putConnection(lookupKey, connection);

        return connection;

    }

    @Override
    public void afterPropertiesSet() {
        loadAllDataSource();
        chooseDefaultDataSource();
        super.afterPropertiesSet();
    }

    @Override
    public void destroy() throws Exception {
        LOGGER.info("dynamic datasource start closing ....");
        for (Map.Entry<String, DataSource> item : dynamicDataSourceMap.entrySet()) {
            this.closeDataSource(item.getKey(), item.getValue());
        }
        LOGGER.info("dynamic datasource all closed success");
    }


    protected synchronized void add(String dataSourceKey, DataSource dataSource) {
        if (dynamicDataSourceMap.containsKey(dataSourceKey)) {
            throw new IllegalStateException("dynamic datasource already exist key:" + dataSourceKey);
        }
        dynamicDataSourceMap.put(dataSourceKey, dataSource);
    }


    protected synchronized void remove(String key) {
        if (dataSourceProperties.getPrimary().equals(key)) {
            throw new IllegalStateException("primary dynamic datasource cannot remove " + key);
        }
        DataSource dataSource = this.dynamicDataSourceMap.remove(key);
        if (dataSource != null) {
            this.closeDataSource(key, dataSource);
        }
    }


    public ConcurrentHashMap<String, DataSource> getDynamicDataSourceMap() {
        return dynamicDataSourceMap;
    }

    private void closeDataSource(String dsName, DataSource dataSource) {
        LOGGER.info("close dynamic datasource :{}", dsName);
        Method closeMethod = ReflectionUtils.findMethod(dataSource.getClass(), "close");
        if (closeMethod != null) {
            try {
                closeMethod.invoke(dataSource);
            } catch (Exception e) {
                LOGGER.error("close dynamic datasource:{}", dsName, e);
            }
        }
    }


    private void chooseDefaultDataSource() {
        DataSource primaryDataSource = dynamicDataSourceMap.get(dataSourceProperties.getPrimary());
        if (primaryDataSource == null) {
            throw new IllegalStateException("未找到首选数据源:" + dataSourceProperties.getPrimary());
        }
        this.setDefaultTargetDataSource(primaryDataSource);
        LOGGER.info("【easy-model】动态数据源--主源:{}", dataSourceProperties.getPrimary());
        LOGGER.info("【easy-model】动态数据源--切换请求头:{}", dataSourceProperties.getHeader());
    }

    private void loadAllDataSource() {
        this.setTargetDataSources(new HashMap<>());
        for (DataSourceProvider provider : dataSourceProviders) {
            dynamicDataSourceMap.putAll(provider.loadDataSources());
        }
        if (CollectionUtils.isEmpty(dynamicDataSourceMap)) {
            throw new IllegalStateException("未读取到动态数据源配置");
        }
        LOGGER.info("【easy-model】动态数据源--加载成功:{}", String.join(",", dynamicDataSourceMap.keySet()));
    }


}
