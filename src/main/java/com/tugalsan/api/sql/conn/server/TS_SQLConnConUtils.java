package com.tugalsan.api.sql.conn.server;

import java.sql.*;
import com.tugalsan.api.log.server.TS_Log;
import com.tugalsan.api.profile.server.melody.*;
import com.tugalsan.api.union.client.TGS_UnionExcuse;
import com.tugalsan.api.function.client.maythrowexceptions.checked.TGS_FuncMTCUtils;

public class TS_SQLConnConUtils {

    private TS_SQLConnConUtils() {

    }

    final private static TS_Log d = TS_Log.of(TS_SQLConnConUtils.class);

    public static boolean scrollingSupported(Connection con) {
        return TGS_FuncMTCUtils.call(() -> con.getMetaData().supportsResultSetType(ResultSet.TYPE_SCROLL_INSENSITIVE));
    }

//    private static void destroy(TS_SQLConnSource source) {
//        if (d.infoEnable) {
//            d.ci("destroy", "source", source.anchor().config.dbName);
//        }
//        TGS_FuncMTCUtils.run(() -> (source.main()).close(true), e -> d.ce("destroy", "INFO: " + e.getMessage()));
//    }

    public static void destroy() {
//        SYNC.forEach(true, item -> destroy(item));
    }

    @Deprecated //DOES IT EVEN WORK?
    public static void destroy(ClassLoader thread_currentThread_getContextClassLoader) {
        var drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            var driver = drivers.nextElement();
            var driverClassName = driver.getClass().getSimpleName();
            if (driver.getClass().getClassLoader() == thread_currentThread_getContextClassLoader) {
                var classLoaderName = thread_currentThread_getContextClassLoader.getName();
                var classLoaderClassName = thread_currentThread_getContextClassLoader.getClass().getSimpleName();
                TGS_FuncMTCUtils.run(() -> {
                    d.cr("destroy", "found", driverClassName, classLoaderName, classLoaderClassName);
                    DriverManager.deregisterDriver(driver);
                    d.cr("destroy", "successful");
                }, e -> d.ce("destroy", "failed", driverClassName, classLoaderName, classLoaderClassName));
            } else {
                d.ci("destroy", "Not deregistering JDBC driver, as it does not belong to this webapp's ClassLoader", driver);
            }
        }
    }

    private static TGS_UnionExcuse<Connection> conProp(TS_SQLConnAnchor anchor) {
        return TGS_FuncMTCUtils.call(() -> {
            Class.forName(TS_SQLConnMethodUtils.getDriver(anchor.config)).getConstructor().newInstance();
            return TGS_UnionExcuse.of(DriverManager.getConnection(anchor.url(), anchor.properties()));
        }, e -> TGS_UnionExcuse.ofExcuse(e));
    }

//    synchronized public static TS_SQLConnSource conPoolPack(TS_SQLConnAnchor anchor) {
//        if (anchor.config.pool_debug && !d.infoEnable) {
//            d.infoEnable = anchor.config.pool_debug;
//        }
//        if (d.infoEnable) {
//            d.ci("conPoolPack", "size", SYNC.size());
//            SYNC.forEach(false, item -> d.ci("conPoolPack", "item", item.anchor().config.dbName));
//        }
//        var source = SYNC.findFirst(c -> Objects.equals(c.anchor(), anchor));
//        if (source != null) {
//            if (isActive(source.main()) && isActive(source.proxy().orElse(null))) {
//                if (d.infoEnable) {
//                    d.cr("conPoolPack", anchor.config.dbName, "ACTIVE");
//                    if (anchor.config.pool_debug) {
//                        TS_SQLConnPoolUtils.printStats(source.main());
//                    }
//                }
//                return source;
//            } else {
//                if (d.infoEnable) {
//                    d.ce("conPoolPack", anchor.config.dbName, "NOT ACTIVE");
//                }
//                destroy(SYNC.removeAndPopFirst(source));
//            }
//        }
//        var ds = new org.apache.tomcat.jdbc.pool.DataSource(anchor.pool());
//        var dsThroughProxy = TS_ProfileMelodyUtils.createProxy(ds);
//        source = new TS_SQLConnSource(anchor, ds, dsThroughProxy);
//        if (d.infoEnable) {
//            d.cr("conPoolPack", anchor.config.dbName, "NEW");
//            if (anchor.config.pool_debug) {
//                TS_SQLConnPoolUtils.printStats(source.main());
//            }
//        }
//        SYNC.add(source);
//        return source;
//    }
//    final private static TS_ThreadSyncLst<TS_SQLConnSource> SYNC = TS_ThreadSyncLst.ofSlowWrite();
//
//    @Deprecated
//    private static boolean isActive(javax.sql.DataSource con) {
//        if (true) {
//            return true;
//        }
//        if (con == null) {
//            return false;
//        }
//        return TGS_FuncMTCUtils.call(() -> {
//            TGS_Tuple1<Boolean> result = new TGS_Tuple1(false);
//            var sqlStmt = "SELECT 'Hello world'  FROM DUAL";
//            TGS_FuncMTCUtils.run(() -> {
//                try (var resultSet = TS_SQLConnStmtUtils.stmtQuery(con.getConnection(), sqlStmt).executeQuery()) {
//                    var rs = new TS_SQLResultSet(resultSet);
//                    var val = rs.str.get(0, 0);
//                    result.value0 = true;
//                }
//            });
//            return result.value0;
//        }, e -> false);
//    }

    public static TGS_UnionExcuse<TS_SQLConnPack> conPack(TS_SQLConnAnchor anchor) {
//        return anchor.config.isPooled ? conPack_pool(anchor) : conPack_prop(anchor);
        return conPack_prop(anchor);
    }

//    private static TGS_UnionExcuse<TS_SQLConnPack> conPack_pool(TS_SQLConnAnchor anchor) {
//        return TGS_FuncMTCUtils.call(() -> {
//            var conPoolPack = conPoolPack(anchor);
//            if (conPoolPack.proxy().isExcuse()) {
//                return conPoolPack.proxy().toExcuse();
//            }
//            var newConPack = new TS_SQLConnPack(
//                    anchor,
//                    conPoolPack.main().getConnection(),
//                    conPoolPack.proxy().value().getConnection()
//            );
//            return TGS_UnionExcuse.of(newConPack);
//        }, e -> TGS_UnionExcuse.ofExcuse(e));
//    }

    private static TGS_UnionExcuse<TS_SQLConnPack> conPack_prop(TS_SQLConnAnchor anchor) {
        return TGS_FuncMTCUtils.call(() -> {
            var u_main_con = conProp(anchor);
            if (u_main_con.isExcuse()) {
                return u_main_con.toExcuse();
            }
            var u_proxy_con = TS_ProfileMelodyUtils.createProxy(u_main_con.value());
            if (u_proxy_con.isExcuse()) {
                return u_proxy_con.toExcuse();
            }
            var newConPack = new TS_SQLConnPack(
                    anchor,
                    u_main_con.value(),
                    u_proxy_con.value()
            );
            return TGS_UnionExcuse.of(newConPack);
        }, e -> TGS_UnionExcuse.ofExcuse(e));
    }
}
