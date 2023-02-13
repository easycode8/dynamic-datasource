package com.easycode8.datasource.dynamic.core.util;


import com.easycode8.datasource.dynamic.core.transaction.ConnectionHolder;
import com.easycode8.datasource.dynamic.core.transaction.ConnectionProxy;
import org.springframework.lang.Nullable;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.sql.SQLException;

public class DynamicDataSourceUtils {
    public static void releaseConnection(@Nullable ConnectionProxy con, @Nullable DataSource dataSource) {
        if (con == null) {
            return;
        }
        if (dataSource != null) {
            ConnectionHolder conHolder = (ConnectionHolder) TransactionSynchronizationManager.getResource(dataSource);
            if (conHolder != null && conHolder.contain(con)) {
                // It's the transactional Connection: Don't close it.
                //conHolder.released();
                return;
            }
        }
        try {
            con.doClose();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }


}
