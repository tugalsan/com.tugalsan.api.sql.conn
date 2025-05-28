package com.tugalsan.api.sql.conn.server;

import java.util.Arrays;

public class TS_SQLConnDestroyUtils {

    private TS_SQLConnDestroyUtils() {

    }

    public static void destroy(TS_SQLConnAnchor... anchors) {
        var isMysqlVariant = Arrays.stream(anchors)
                .anyMatch(anchor -> anchor.config.method == TS_SQLConnMethodUtils.METHOD_MARIADB() || anchor.config.method == TS_SQLConnMethodUtils.METHOD_MYSQL());
        if (isMysqlVariant) {
//            try {
//                AbandonedConnectionCleanupThread.shutdown();
//            } catch (InterruptedException e) {
//            }
        }

    }
}
