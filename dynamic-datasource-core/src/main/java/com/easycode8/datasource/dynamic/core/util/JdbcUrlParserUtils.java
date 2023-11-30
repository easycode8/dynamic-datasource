package com.easycode8.datasource.dynamic.core.util;

import java.net.URI;
import java.net.URISyntaxException;

public class JdbcUrlParserUtils {

    /**
     * 从jdbc中提取数据库类型
     * @param jdbcUrl
     * @return
     */
    public static String extractDatabaseType(String jdbcUrl) {
        try {
            // 如果 URL 包含 log4jdbc，则截取其后的部分
            if (jdbcUrl.contains(":log4jdbc:")) {
                jdbcUrl = jdbcUrl.split(":log4jdbc:")[1];
            }

            URI uri = new URI(jdbcUrl.replace("jdbc:", ""));
            String subprotocol = uri.getScheme();
            if (subprotocol != null) {
                return subprotocol.toLowerCase();
            }
        } catch (URISyntaxException e) {
            throw new IllegalStateException("extract database type error");
        }
        return null;
    }

    public static void main(String[] args) {
        String jdbcUrl = "jdbc:log4jdbc:postgresql://127.0.0.1:6543/db_app?useUnicode=true&characterEncoding=utf8";
        String databaseType = extractDatabaseType(jdbcUrl);
        System.out.println("Database Type: " + databaseType);


        String jdbcUrl1 = "jdbc:mysql://localhost:3306/mydatabase";
        String databaseType1 = extractDatabaseType(jdbcUrl1);
        System.out.println("Database Type: " + databaseType1);


        String jdbcUrl2 = "jdbc:oracle:thin:@192.168.3.209/zoearch";
        String databaseType2 = extractDatabaseType(jdbcUrl2);
        System.out.println("Database Type: " + databaseType2);

        String jdbcUrl3 = "jdbc:h2:mem:test01";
        String databaseType3 = extractDatabaseType(jdbcUrl3);
        System.out.println("Database Type: " + databaseType3);
    }
}
