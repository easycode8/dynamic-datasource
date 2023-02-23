package com.easycode8.datasource.dynamic.core;

/**
 * 数据源配置信息
 */
public class DataSourceInfo {

    private String key;
    private String driverClassName;
    private String url;
    private String username;
    private String password;
    /** 数据源类型*/
    private String type = "default";

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static Builder builder() {
        return new Builder();
    }


    public static final class Builder {
        private String key;
        private String driverClassName;
        private String url;
        private String username;
        private String password;
        private String type = "default";

        private Builder() {
        }

        public static Builder aDataSourceInfo() {
            return new Builder();
        }

        public Builder key(String key) {
            this.key = key;
            return this;
        }

        public Builder driverClassName(String driverClassName) {
            this.driverClassName = driverClassName;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public DataSourceInfo build() {
            DataSourceInfo dataSourceInfo = new DataSourceInfo();
            dataSourceInfo.setKey(key);
            dataSourceInfo.setDriverClassName(driverClassName);
            dataSourceInfo.setUsername(username);
            dataSourceInfo.setPassword(password);
            dataSourceInfo.setType(type);
            dataSourceInfo.url = this.url;
            return dataSourceInfo;
        }
    }
}
