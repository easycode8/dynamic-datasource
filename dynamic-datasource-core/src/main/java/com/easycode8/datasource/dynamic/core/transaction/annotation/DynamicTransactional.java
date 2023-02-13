package com.easycode8.datasource.dynamic.core.transaction.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DynamicTransactional {
    String value() default "";
}
