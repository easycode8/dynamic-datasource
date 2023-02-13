package com.easycode8.datasource.dynamic.core;


import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties("spring.datasource.dynamic")
public class DynamicDataSourceProperties {
    /** 首选主数据源的标识*/
    private String primary = "master";
    /** 请求头数据源标记的变量*/
    private String header = "";
    /** 是否启动时候检查所有数据源有效性,默认false,不检查*/
    private Boolean check = false;
    /** 数据源集合
     * spring:
     *   datasource:
     *     dynamic:
     *       header: db-type
     *       primary: master
     *       datasource:
     *         master:
     *           driver-class-name: org.h2.Driver
     *           jdbc-url: jdbc:h2:mem:test
     *           username: root
     *           password: 123456
     *         db1:
     *           driver-class-name: org.h2.Driver
     *           jdbc-url: jdbc:h2:mem:test1
     *           username: root
     *           password: 123456
     *
     * */
    private Map<String, DataSourceInfo> datasource;

    public Map<String, DataSourceInfo> getDatasource() {
        return datasource;
    }

    public void setDatasource(Map<String, DataSourceInfo> datasource) {
        this.datasource = datasource;
    }

    public String getPrimary() {
        return primary;
    }

    public void setPrimary(String primary) {
        this.primary = primary;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public Boolean getCheck() {
        return check;
    }

    public void setCheck(Boolean check) {
        this.check = check;
    }
}
