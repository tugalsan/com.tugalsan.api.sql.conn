package com.tugalsan.api.sql.conn.server;

import com.tugalsan.api.function.client.maythrowexceptions.checked.TGS_FuncMTCUtils;
import com.tugalsan.api.log.server.TS_Log;
import java.sql.DriverManager;
import java.util.Arrays;

public class TS_SQLConnDestroyUtils {

    final private static TS_Log d = TS_Log.of(TS_SQLConnDestroyUtils.class);

    private TS_SQLConnDestroyUtils() {

    }

    @Deprecated //WHY TO USE. NOT GOOD WITH MULTI WAR PROJECTS
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
            TGS_FuncMTCUtils.run(() -> {
                DriverManager.deregisterDriver(driver);
                d.cr("Driver deregistered", driver);
            }, e -> d.ce("Error deregistering driver!", driver, e.getMessage()));
        });
    }
}
