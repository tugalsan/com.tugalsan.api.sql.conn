package com.tugalsan.api.sql.conn.server;

import com.tugalsan.api.list.client.TGS_ListUtils;
import java.sql.*;
import java.util.*;
import org.apache.tomcat.jdbc.pool.*;
import com.tugalsan.api.log.server.TS_Log;
import com.tugalsan.api.profile.server.melody.*;
import com.tugalsan.api.string.client.TGS_StringUtils;
import com.tugalsan.api.thread.server.sync.TS_ThreadSyncLst;
import com.tugalsan.api.union.client.TGS_UnionExcuse;
import com.tugalsan.api.union.client.TGS_UnionExcuseVoid;
import java.lang.reflect.InvocationTargetException;

public class TS_SQLConnConUtils {

    final private static TS_Log d = TS_Log.of(TS_SQLConnConUtils.class);

    public static TGS_UnionExcuseVoid scrollingSupported(Connection con) {
        try {
            var result = con.getMetaData().supportsResultSetType(ResultSet.TYPE_SCROLL_INSENSITIVE);
            if (!result) {
                return TGS_UnionExcuseVoid.ofExcuse(d.className, "scrollingSupported", "result is false");
            }
            return TGS_UnionExcuseVoid.ofVoid();
        } catch (SQLException ex) {
            return TGS_UnionExcuseVoid.ofExcuse(ex);
        }
    }

    @Deprecated
    public static TGS_UnionExcuseVoid valid(Connection con, int timeoutSeconds) {
        try {
            var result = con.isValid(timeoutSeconds);
            if (!result) {
                return TGS_UnionExcuseVoid.ofExcuse(d.className, "valid", "result is false");
            }
            return TGS_UnionExcuseVoid.ofVoid();
        } catch (SQLException ex) {
            return TGS_UnionExcuseVoid.ofExcuse(ex);
        }
    }

    public static void destroy() {
        SYNC.forEach(item -> {
            ((org.apache.tomcat.jdbc.pool.DataSource) item.main_con).close(true);
            ((org.apache.tomcat.jdbc.pool.DataSource) item.main_con).close();
            ((org.apache.tomcat.jdbc.pool.DataSource) item.proxy_con).close(true);
            ((org.apache.tomcat.jdbc.pool.DataSource) item.proxy_con).close();
        });
    }

    @Deprecated //DOES IT EVEN WORK?
    public static TGS_UnionExcuseVoid destroy(ClassLoader thread_currentThread_getContextClassLoader) {
        var drivers = DriverManager.getDrivers();
        List<String> errors = TGS_ListUtils.of();
        while (drivers.hasMoreElements()) {
            var driver = drivers.nextElement();
            if (driver.getClass().getClassLoader() == thread_currentThread_getContextClassLoader) {
                try {
                    DriverManager.deregisterDriver(driver);
                } catch (SQLException ex) {
                    var driverClassName = driver.getClass().getSimpleName();
                    var classLoaderName = thread_currentThread_getContextClassLoader.getName();
                    var classLoaderClassName = thread_currentThread_getContextClassLoader.getClass().getSimpleName();
                    errors.add(TGS_StringUtils.concat(
                            "driverClassName:", driverClassName,
                            ", classLoaderName:", classLoaderName,
                            ", classLoaderClassName:", classLoaderClassName
                    ));
                }
            } else {
                d.ci("destroy", "Not deregistering JDBC driver, as it does not belong to this webapp's ClassLoader", driver);
            }
        }
        if (errors.isEmpty()) {
            return TGS_UnionExcuseVoid.ofVoid();
        } else {
            var sj = new StringJoiner("|");
            errors.forEach(e -> sj.add(e));
            return TGS_UnionExcuseVoid.ofExcuse(d.className, "destroy", sj.toString());
        }
    }

    private static TGS_UnionExcuse<Connection> conProp(TS_SQLConnAnchor anchor) {
        try {
            Class.forName(TS_SQLConnMethodUtils.getDriver(anchor.config)).getConstructor().newInstance();
            return TGS_UnionExcuse.of(DriverManager.getConnection(anchor.url(), anchor.properties()));
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException | SQLException ex) {
            return TGS_UnionExcuse.ofExcuse(ex);
        }
    }

    private static TGS_UnionExcuse<Connection> conPool(TS_SQLConnAnchor anchor) {
        try {
            var u_ds = ds(anchor);
            if (u_ds.isExcuse()) {
                return u_ds.toExcuse();
            }
            return TGS_UnionExcuse.of(u_ds.value().getConnection());
        } catch (SQLException ex) {
            return TGS_UnionExcuse.ofExcuse(ex);
        }
    }

    private static TGS_UnionExcuse<javax.sql.DataSource> ds(TS_SQLConnAnchor anchor) {
        var pack = SYNC.findFirst(c -> Objects.equals(c.anchor, anchor));
        if (pack != null) {
            return TGS_UnionExcuse.of(pack.main_con);
        }
        var main_con = new DataSource(anchor.pool());
        var u_proxy_con = TS_ProfileMelodyUtils.createProxy(main_con);
        if (u_proxy_con.isExcuse()) {
            return u_proxy_con.toExcuse();
        }
        SYNC.add(new ConPack(anchor, main_con, u_proxy_con.value()));
        return TGS_UnionExcuse.of(main_con);
    }

    private record ConPack(TS_SQLConnAnchor anchor, javax.sql.DataSource main_con, javax.sql.DataSource proxy_con) {

    }
    final private static TS_ThreadSyncLst<ConPack> SYNC = TS_ThreadSyncLst.of();

    public static TGS_UnionExcuse<TS_SQLConnPack> conPack(TS_SQLConnAnchor anchor) {
        var u_main_con = anchor.config.isPooled ? conPool(anchor) : conProp(anchor);
        if (u_main_con.isExcuse()) {
            return u_main_con.toExcuse();
        }
        var u_proxy_con = TS_ProfileMelodyUtils.createProxy(u_main_con.value());
        if (u_proxy_con.isExcuse()) {
            return u_proxy_con.toExcuse();
        }
        return TGS_UnionExcuse.of(new TS_SQLConnPack(anchor, u_main_con.value(), u_proxy_con.value()));
    }

    public static String con_SKIP_TROW() {
        return "Could not create connection to database server. Attempted reconnect 3 times. Giving up.";
    }
}
