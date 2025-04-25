package com.tugalsan.api.sql.conn.server;

import java.sql.*;
import java.util.*;
import org.apache.tomcat.jdbc.pool.*;
import com.tugalsan.api.log.server.TS_Log;
import com.tugalsan.api.profile.server.melody.*;
import com.tugalsan.api.thread.server.sync.TS_ThreadSyncWait;
import com.tugalsan.api.thread.server.sync.TS_ThreadSyncLst;
import com.tugalsan.api.union.client.TGS_UnionExcuse;

import com.tugalsan.api.function.client.maythrowexceptions.unchecked.TGS_FuncMTU;
import com.tugalsan.api.function.client.maythrowexceptions.checked.TGS_FuncMTCUtils;
import com.tugalsan.api.sql.resultset.server.TS_SQLResultSet;
import com.tugalsan.api.tuple.client.TGS_Tuple1;

public class TS_SQLConnConUtils {

    private TS_SQLConnConUtils() {

    }

    final private static TS_Log d = TS_Log.of(TS_SQLConnConUtils.class);

    public static boolean scrollingSupported(Connection con) {
        return TGS_FuncMTCUtils.call(() -> con.getMetaData().supportsResultSetType(ResultSet.TYPE_SCROLL_INSENSITIVE));
    }

    @Deprecated
    public static boolean valid(Connection con0, int timeoutSeconds) {
        return TGS_FuncMTCUtils.call(() -> {
            try (var con = con0) {
                return con.isValid(timeoutSeconds);
            }
        });
    }

    public static void destroy() {
        SYNC.forEach(true, item -> {
            TGS_FuncMTCUtils.run(() -> ((org.apache.tomcat.jdbc.pool.DataSource) item.main()).close(true), e -> TGS_FuncMTU.empty.run());
            TGS_FuncMTCUtils.run(() -> ((org.apache.tomcat.jdbc.pool.DataSource) item.main()).close(), e -> TGS_FuncMTU.empty.run());
            TGS_FuncMTCUtils.run(() -> ((org.apache.tomcat.jdbc.pool.DataSource) item.main()).close(true), e -> TGS_FuncMTU.empty.run());
            TGS_FuncMTCUtils.run(() -> ((org.apache.tomcat.jdbc.pool.DataSource) item.main()).close(), e -> TGS_FuncMTU.empty.run());
        });
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

    private static TGS_UnionExcuse<Connection> conPool(TS_SQLConnAnchor anchor) {
        return TGS_FuncMTCUtils.call(() -> {
            var u = throughProxy(anchor);
            if (u.isExcuse()) {
                return u.toExcuse();
            }
            return TGS_UnionExcuse.of(u.value().getConnection());
        }, e -> TGS_UnionExcuse.ofExcuse(e));
    }

    private static TGS_UnionExcuse<javax.sql.DataSource> throughProxy(TS_SQLConnAnchor anchor) {
        var pack = SYNC.findFirst(c -> Objects.equals(c.anchor(), anchor));
        if (pack != null) {
            if (isActive(pack.main()) && isActive(pack.proxy().orElse(null))) {
                return TGS_UnionExcuse.of(pack.main());
            } else {
                SYNC.removeAndPopFirst(pack);
            }
        }
        var ds = new DataSource(anchor.pool());
        var dsThroughProxy = TS_ProfileMelodyUtils.createProxy(ds);
        SYNC.add(new TS_ConnPackSource(anchor, ds, dsThroughProxy));
        return dsThroughProxy;
    }
    final private static TS_ThreadSyncLst<TS_ConnPackSource> SYNC = TS_ThreadSyncLst.ofSlowWrite();

    public static TGS_UnionExcuse<TS_SQLConnPack> conPack(TS_SQLConnAnchor anchor) {
        return TGS_FuncMTCUtils.call(() -> {
            var u_main_con = anchor.config.isPooled ? conPool(anchor) : conProp(anchor);
            var u_proxy_con = TS_ProfileMelodyUtils.createProxy(u_main_con.value());
            return TGS_UnionExcuse.of(new TS_SQLConnPack(anchor, u_main_con.value(), u_proxy_con.value()));
        }, e -> {
            return TGS_FuncMTCUtils.call(() -> {
                TS_ThreadSyncWait.seconds(d.className, null, 3);
                var u_main_con = anchor.config.isPooled ? conPool(anchor) : conProp(anchor);
                if (u_main_con.isExcuse()) {
                    return u_main_con.toExcuse();
                }
                var u_proxy_con = TS_ProfileMelodyUtils.createProxy(u_main_con.value());
                if (u_proxy_con.isExcuse()) {
                    return u_proxy_con.toExcuse();
                }
                return TGS_UnionExcuse.of(new TS_SQLConnPack(anchor, u_main_con.value(), u_proxy_con.value()));
            });
        });
    }

    private static boolean isActive(javax.sql.DataSource con) {
        if (con == null) {
            return false;
        }
        return TGS_FuncMTCUtils.call(() -> {
            TGS_Tuple1<Boolean> result = new TGS_Tuple1(false);
            var sqlStmt = "SELECT 'Hello world'  FROM DUAL";
            TGS_FuncMTCUtils.run(() -> {
                try (var resultSet = TS_SQLConnStmtUtils.stmtQuery(con.getConnection(), sqlStmt).executeQuery()) {
                    var rs = new TS_SQLResultSet(resultSet);
                    var val = rs.str.get(0, 0);
                    result.value0 = true;
                }
            });
            return result.value0;
        }, e -> false);
    }

    public static String con_SKIP_TROW() {
        return "Could not create connection to database server. Attempted reconnect 3 times. Giving up.";
    }
}
