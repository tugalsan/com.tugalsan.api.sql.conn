package com.tugalsan.api.sql.conn.server;

import com.tugalsan.api.log.server.TS_Log;
import org.apache.tomcat.jdbc.pool.PoolProperties;

public class TS_SQLConnPoolUtils {

    private TS_SQLConnPoolUtils() {

    }
    final private static TS_Log d = TS_Log.of(true, TS_SQLConnPoolUtils.class);

    //https://tomcat.apache.org/tomcat-11.0-doc/jdbc-pool.html
    public static PoolProperties create(TS_SQLConnConfig config) {
        var pool = new PoolProperties();
        pool.setUrl(TS_SQLConnURLUtils.create(config));
        pool.setDriverClassName(TS_SQLConnMethodUtils.getDriver(config));
        if (config.dbUser == null || config.dbUser.equals("") || config.dbPassword == null) {
        } else {
            pool.setUsername(config.dbUser);
            pool.setPassword(config.dbPassword);
        }

        pool.setJmxEnabled(true);
        pool.setTestWhileIdle(false);
        pool.setTestOnBorrow(true);
        pool.setValidationQuery("SELECT 'Hello world'  FROM DUAL");//SELECT 1
        pool.setValidationInterval(30000);
        pool.setValidationQueryTimeout(10);
        pool.setTestOnReturn(false);
        pool.setTimeBetweenEvictionRunsMillis(30000);

        pool.setMaxActive(100);
        pool.setInitialSize(1);//x < setMaxActive

        pool.setMaxWait(10000);
        pool.setRemoveAbandonedTimeout(60);//seconds
        pool.setMinEvictableIdleTimeMillis(30000);

        pool.setMinIdle(1);// minIdle < x < maxIdle & maxActive
        pool.setMaxIdle(8);//x < setMaxActive
        pool.setLogAbandoned(false);
        pool.setRemoveAbandoned(true);

        pool.setJdbcInterceptors(
                "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"
                //                + "org.apache.tomcat.jdbc.pool.interceptor.ResetAbandonedTimer;"
                + "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");

        return pool;
    }

    public static void printStats(org.apache.tomcat.jdbc.pool.DataSource ds) {
        d.ci("printStats", "ds.getAbandonWhenPercentageFull()", ds.getAbandonWhenPercentageFull());
        d.ci("printStats", "ds.getActive", ds.getActive());
        d.ci("printStats", "ds.getBorrowedCount()", ds.getBorrowedCount());
        d.ci("printStats", "ds.getCreatedCount()", ds.getCreatedCount());
        d.ci("printStats", "ds.getIdle()", ds.getIdle());
        d.ci("printStats", "ds.getInitialSize()", ds.getInitialSize());
        d.ci("printStats", "ds.getNumIdle()", ds.getNumIdle());
        d.ci("printStats", "ds.getNumActive()", ds.getNumActive());
        d.ci("printStats", "ds.getPoolSize()", ds.getPoolSize());
        d.ci("printStats", "ds.getReconnectedCount()", ds.getReconnectedCount());
        d.ci("printStats", "ds.getReleasedCount()", ds.getReleasedCount());
        d.ci("printStats", "ds.getReleasedIdleCount()", ds.getReleasedIdleCount());
        d.ci("printStats", "ds.getRemoveAbandonedCount()", ds.getRemoveAbandonedCount());
        d.ci("printStats", "ds.getReturnedCount()", ds.getReturnedCount());
        d.ci("printStats", "ds.getSize()", ds.getSize());
        d.ci("printStats", "ds.getWaitCount()", ds.getWaitCount());
    }
}
