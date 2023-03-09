package com.easycode8.datasource.dynamic.web.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.datasource.dynamic.web")
public class DynamicDataSourceWebProperties {
    /**账号*/
    private String username = "admin";
    /**密码*/
    private String password = "admin123";
    /**是否开启web登入认证*/
    private Boolean enableBasicAuth = true;

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

    public Boolean getEnableBasicAuth() {
        return enableBasicAuth;
    }

    public void setEnableBasicAuth(Boolean enableBasicAuth) {
        this.enableBasicAuth = enableBasicAuth;
    }
}
