package com.easycode8.datasource.dynamic.core.transaction.annotation;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(DynamicTransactionManagementConfiguration.class)
public @interface EnableDynamicTransactionManagement {
}
