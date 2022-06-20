package com.tugalsan.api.sql.conn.server;

import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class TS_SQLConnPropsUtils {

    public static Properties create(TS_SQLConnConfig config) {
        var prop = new Properties();
        if (config.charsetUTF8) {
            prop.put("charSet", StandardCharsets.UTF_8.name());
        }
        if (config.dbUser == null || config.dbUser.equals("") || config.dbPassword == null) {
        } else {
            prop.put("user", config.dbUser);
            prop.put("password", config.dbPassword);
        }
        return prop;
    }
}
