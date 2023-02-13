package com.easycode8.datasource.dynamic.core.util;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class SpringExceptionUtils {

    public static void handleRootException(Throwable e, Consumer<Throwable> consumer) {
        while (e != null) {
            Throwable cause = e.getCause();
            if (cause == null) {
                //System.out.println(e.getClass());

                consumer.accept(e);
            }
            e = cause;
        }
    }

    public  static  <T> T runIgnoreException(Supplier<T> supplier, Consumer<Throwable> consumer) {
        T t = null;
        try {
              t = supplier.get();
        } catch (Throwable e) {
            handleRootException(e, consumer);
        }
        return t;
    }


}
