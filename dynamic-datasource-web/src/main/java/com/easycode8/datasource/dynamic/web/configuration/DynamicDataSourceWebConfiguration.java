package com.easycode8.datasource.dynamic.web.configuration;

import com.easycode8.datasource.dynamic.web.LoginController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class DynamicDataSourceWebConfiguration {

    @Bean
    public LoginController loginController() {
        return new LoginController();
    }
}
