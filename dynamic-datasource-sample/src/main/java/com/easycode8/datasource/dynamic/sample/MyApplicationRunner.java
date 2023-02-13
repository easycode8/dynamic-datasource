package com.easycode8.datasource.dynamic.sample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.net.InetAddress;

@Component//被spring容器管理
@Order(1)//如果多个自定义ApplicationRunner，用来标明执行顺序
public class MyApplicationRunner implements ApplicationRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyApplicationRunner.class);

    @Autowired
    private TomcatServletWebServerFactory tomcatServletWebServerFactory;
    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {

        this.showUrl();
    }

    private void showUrl() throws Exception{
        String host = InetAddress.getLocalHost().getHostAddress();
        int port = tomcatServletWebServerFactory.getPort();
        String contextPath = tomcatServletWebServerFactory.getContextPath();
        LOGGER.info("欢迎访问 http://"+host+":"+port+contextPath+"/");
    }

}