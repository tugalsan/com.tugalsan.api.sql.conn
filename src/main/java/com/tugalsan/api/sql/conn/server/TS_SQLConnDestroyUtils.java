package com.tugalsan.api.sql.conn.server;

import com.tugalsan.api.function.client.TGS_FuncUtils;
import com.tugalsan.api.log.server.TS_Log;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;

public class TS_SQLConnDestroyUtils {

    final private static TS_Log d = TS_Log.of(TS_SQLConnDestroyUtils.class);

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
        DriverManager.getDrivers().asIterator().forEachRemaining(driver -> {
            try {
                DriverManager.deregisterDriver(driver);
                d.cr("Driver deregistered", driver);
            } catch (SQLException e) {
                d.ce("Error deregistering driver!", driver, e.getMessage());
                TGS_FuncUtils.throwIfInterruptedException(e);
            }
        });
    }
}
