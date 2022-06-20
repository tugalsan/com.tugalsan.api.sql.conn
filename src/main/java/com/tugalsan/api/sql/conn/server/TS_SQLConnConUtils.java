package com.tugalsan.api.sql.conn.server;

import java.sql.*;
import java.util.*;
import org.apache.tomcat.jdbc.pool.*;
import com.tugalsan.api.list.server.*;
import com.tugalsan.api.log.server.TS_Log;
import com.tugalsan.api.pack.client.*;
import com.tugalsan.api.profile.server.melody.*;

public class TS_SQLConnConUtils {

    final private static TS_Log d = TS_Log.of(TS_SQLConnConUtils.class.getSimpleName());

    public static boolean scrollingSupported(Connection con) {
        try {
            return con.getMetaData().supportsResultSetType(ResultSet.TYPE_SCROLL_INSENSITIVE);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(TS_SQLConnConUtils.class.getSimpleName() + ".scrollingSupported = ?");
        }
    }

    @Deprecated
    public static boolean valid(Connection con0, int timeoutSeconds) {
        try ( var con = con0) {
            return con.isValid(timeoutSeconds);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void destroy() {
        SYNC.forEach(item -> {
            try {
                ((org.apache.tomcat.jdbc.pool.DataSource) item.value1).close(true);
            } catch (Exception e) {
            }
            try {
                ((org.apache.tomcat.jdbc.pool.DataSource) item.value1).close();
            } catch (Exception e) {
            }
            try {
                ((org.apache.tomcat.jdbc.pool.DataSource) item.value2).close(true);
            } catch (Exception e) {
            }
            try {
                ((org.apache.tomcat.jdbc.pool.DataSource) item.value2).close();
            } catch (Exception e) {
            }
        });
    }

    @Deprecated //DOES IT EVEN WORK?
    public static void destroy(ClassLoader thread_currentThread_getContextClassLoader
    ) {
        var drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            var driver = drivers.nextElement();
            var driverClassName = driver.getClass().getSimpleName();
            if (driver.getClass().getClassLoader() == thread_currentThread_getContextClassLoader) {
                var classLoaderName = thread_currentThread_getContextClassLoader.getName();
                var classLoaderClassName = thread_currentThread_getContextClassLoader.getClass().getSimpleName();
                try {
                    d.cr("destroy", "found", driverClassName, classLoaderName, classLoaderClassName);
                    DriverManager.deregisterDriver(driver);
                    d.cr("destroy", "successful");
                } catch (SQLException e) {
                    d.ce("destroy", "failed", driverClassName, classLoaderName, classLoaderClassName);
                }
            } else {
                d.ci("destroy", "Not deregistering JDBC driver, as it does not belong to this webapp's ClassLoader", driver);
            }
        }
    }

    private static Connection conProp(TS_SQLConnAnchor anchor) {
        try {
            Class.forName(TS_SQLConnMethodUtils.getDriver(anchor.config)).getConstructor().newInstance();
            return DriverManager.getConnection(anchor.url(), anchor.properties());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Connection conPool(TS_SQLConnAnchor anchor) {
        try {
            return ds(anchor).getConnection();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
    final private static TS_ListSync<TGS_Pack3<TS_SQLConnAnchor, javax.sql.DataSource, javax.sql.DataSource>> SYNC = new TS_ListSync();

    public static TS_SQLConnPack conPack(TS_SQLConnAnchor anchor) {
        var main_con = anchor.config.isPooled ? conPool(anchor) : conProp(anchor);
        var proxy_con = TS_ProfileMelodyUtils.createProxy(main_con);
        return new TS_SQLConnPack(anchor, main_con, proxy_con);
    }

    public static String con_SKIP_TROW() {
        return "Could not create connection to database server. Attempted reconnect 3 times. Giving up.";
    }
}
