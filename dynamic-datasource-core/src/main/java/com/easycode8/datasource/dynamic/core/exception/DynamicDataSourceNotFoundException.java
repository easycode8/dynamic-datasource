package com.easycode8.datasource.dynamic.core.exception;

import org.slf4j.helpers.MessageFormatter;

public class DynamicDataSourceNotFoundException extends RuntimeException{
    private final String message;
    private final String[] variable;

    public DynamicDataSourceNotFoundException(String message, String... variable) {
        super(MessageFormatter.arrayFormat(message, variable).getMessage());
        this.message = message;
        this.variable = variable;
    }

    public DynamicDataSourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
        this.variable = null;
    }

    public DynamicDataSourceNotFoundException(String message) {
        super(message);
        this.message = message;
        this.variable = null;
    }
}
