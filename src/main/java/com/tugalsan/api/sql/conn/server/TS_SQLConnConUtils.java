package com.tugalsan.api.sql.conn.server;

import java.sql.*;
import java.util.*;
import org.apache.tomcat.jdbc.pool.*;
import com.tugalsan.api.list.server.*;
import com.tugalsan.api.log.server.TS_Log;
import com.tugalsan.api.pack.client.*;
import com.tugalsan.api.profile.server.melody.*;
import com.tugalsan.api.unsafe.client.*;

public class TS_SQLConnConUtils {

    final private static TS_Log d = TS_Log.of(TS_SQLConnConUtils.class);

    public static boolean scrollingSupported(Connection con) {
        return TGS_UnSafe.compile(() -> con.getMetaData().supportsResultSetType(ResultSet.TYPE_SCROLL_INSENSITIVE));
    }

    @Deprecated
    public static boolean valid(Connection con0, int timeoutSeconds) {
        return TGS_UnSafe.compile(() -> {
            try ( var con = con0) {
                return con.isValid(timeoutSeconds);
            }
        });
    }

    public static void destroy() {
        SYNC.forEach(item -> {
            TGS_UnSafe.execute(() -> ((org.apache.tomcat.jdbc.pool.DataSource) item.value1).close(true), e -> TGS_UnSafe.doNothing());
            TGS_UnSafe.execute(() -> ((org.apache.tomcat.jdbc.pool.DataSource) item.value1).close(), e -> TGS_UnSafe.doNothing());
            TGS_UnSafe.execute(() -> ((org.apache.tomcat.jdbc.pool.DataSource) item.value2).close(true), e -> TGS_UnSafe.doNothing());
            TGS_UnSafe.execute(() -> ((org.apache.tomcat.jdbc.pool.DataSource) item.value2).close(), e -> TGS_UnSafe.doNothing());
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
                TGS_UnSafe.execute(() -> {
                    d.cr("destroy", "found", driverClassName, classLoaderName, classLoaderClassName);
                    DriverManager.deregisterDriver(driver);
                    d.cr("destroy", "successful");
                }, e -> d.ce("destroy", "failed", driverClassName, classLoaderName, classLoaderClassName));
            } else {
                d.ci("destroy", "Not deregistering JDBC driver, as it does not belong to this webapp's ClassLoader", driver);
            }
        }
    }

    private static Connection conProp(TS_SQLConnAnchor anchor) {
        return TGS_UnSafe.compile(() -> {
            Class.forName(TS_SQLConnMethodUtils.getDriver(anchor.config)).getConstructor().newInstance();
            return DriverManager.getConnection(anchor.url(), anchor.properties());
        });
    }

    private static Connection conPool(TS_SQLConnAnchor anchor) {
        return TGS_UnSafe.compile(() -> ds(anchor).getConnection());
    }

    private static javax.sql.DataSource ds(TS_SQLConnAnchor anchor) {
        var pack = SYNC.findFirst(c -> Objects.equals(c.value0, anchor));
        if (pack != null) {
            return pack.value1;
        }
        var ds = new DataSource(anchor.pool());
        var dsProxy = TS_ProfileMelodyUtils.createProxy(ds);
        SYNC.add(new TGS_Pack3(anchor, ds, dsProxy));
        return dsProxy;
    }
    final private static TS_ThreadSafeLst<TGS_Pack3<TS_SQLConnAnchor, javax.sql.DataSource, javax.sql.DataSource>> SYNC = new TS_ThreadSafeLst();

    public static TS_SQLConnPack conPack(TS_SQLConnAnchor anchor) {
        var main_con = anchor.config.isPooled ? conPool(anchor) : conProp(anchor);
        var proxy_con = TS_ProfileMelodyUtils.createProxy(main_con);
        return new TS_SQLConnPack(anchor, main_con, proxy_con);
    }

    public static String con_SKIP_TROW() {
        return "Could not create connection to database server. Attempted reconnect 3 times. Giving up.";
    }
}
