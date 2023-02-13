package com.easycode8.datasource.dynamic.core.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DynamicSource {
    /**
     * 支持定义数据源类型/spring spel name
     * */
    String value() default "";
    String spelExpression() default "";
}
