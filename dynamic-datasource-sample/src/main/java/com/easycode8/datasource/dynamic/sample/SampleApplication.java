package com.easycode8.datasource.dynamic.sample;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;


@EnableSwagger2WebMvc //2.10.5版本用于替换@EnableSwagger2
@SpringBootApplication
@MapperScan("com.easycode8.datasource.dynamic.sample.mapper")
public class SampleApplication {
    public static void main(String[] args) {
        SpringApplication.run(SampleApplication.class, args);
    }
}
