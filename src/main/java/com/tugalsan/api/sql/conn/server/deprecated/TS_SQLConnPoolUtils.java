package com.tugalsan.api.sql.conn.server.deprecated;

//import com.tugalsan.api.log.server.TS_Log;
//import org.apache.tomcat.jdbc.pool.PoolProperties;

@Deprecated //NO PERFORMANCE GAIN
public class TS_SQLConnPoolUtils {

    private TS_SQLConnPoolUtils() {

    }
//    final private static TS_Log d = TS_Log.of(TS_SQLConnPoolUtils.class);
//
//    //https://tomcat.apache.org/tomcat-11.0-doc/jdbc-pool.html
//    public static PoolProperties create(TS_SQLConnConfig config) {
//        var pool = new PoolProperties();
//        pool.setUrl(TS_SQLConnURLUtils.create(config));
//        pool.setDriverClassName(TS_SQLConnMethodUtils.getDriver(config));
//        if (config.dbUser == null || config.dbUser.equals("") || config.dbPassword == null) {
//        } else {
//            pool.setUsername(config.dbUser);
//            pool.setPassword(config.dbPassword);
//        }
//
//        pool.setJmxEnabled(true);
//        pool.setTestWhileIdle(false);
//        pool.setTestOnBorrow(true);
//        pool.setValidationQuery("SELECT 'Hello world'  FROM DUAL");//SELECT 1
//        pool.setValidationInterval(3000);
//        pool.setValidationQueryTimeout(10);
//        pool.setTestOnReturn(false);
//        pool.setTimeBetweenEvictionRunsMillis(30000);
//
//        pool.setMaxActive(config.pool_concurrent);//my.ini > [mysql] > max_connections=x & max_user_connections=x
//        pool.setInitialSize(1);//x < setMaxActive
//
//        pool.setMaxWait(30000);
//        pool.setRemoveAbandonedTimeout(60);//seconds
//        pool.setMinEvictableIdleTimeMillis(30000);
//
//        pool.setMinIdle(1);// minIdle < x < maxIdle & maxActive
//        pool.setMaxIdle(Math.min(config.pool_concurrent, 4));//x < setMaxActive
//        pool.setLogAbandoned(false);
//        pool.setRemoveAbandoned(true);
//
//        pool.setJdbcInterceptors(
//                "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"
//                //                + "org.apache.tomcat.jdbc.pool.interceptor.ResetAbandonedTimer;"
//                + "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");
//
//        return pool;
//    }
//
//    public static void printStats(org.apache.tomcat.jdbc.pool.DataSource ds) {
//        d.cr("printStats", "ds.getAbandonWhenPercentageFull()", ds.getAbandonWhenPercentageFull());
//        d.cr("printStats", "ds.getActive", ds.getActive());
//        d.cr("printStats", "ds.getBorrowedCount()", ds.getBorrowedCount());
//        d.cr("printStats", "ds.getCreatedCount()", ds.getCreatedCount());
//        d.cr("printStats", "ds.getIdle()", ds.getIdle());
//        d.cr("printStats", "ds.getInitialSize()", ds.getInitialSize());
//        d.cr("printStats", "ds.getNumIdle()", ds.getNumIdle());
//        d.cr("printStats", "ds.getNumActive()", ds.getNumActive());
//        d.cr("printStats", "ds.getPoolSize()", ds.getPoolSize());
//        d.cr("printStats", "ds.getReconnectedCount()", ds.getReconnectedCount());
//        d.cr("printStats", "ds.getReleasedCount()", ds.getReleasedCount());
//        d.cr("printStats", "ds.getReleasedIdleCount()", ds.getReleasedIdleCount());
//        d.cr("printStats", "ds.getRemoveAbandonedCount()", ds.getRemoveAbandonedCount());
//        d.cr("printStats", "ds.getReturnedCount()", ds.getReturnedCount());
//        d.cr("printStats", "ds.getSize()", ds.getSize());
//        d.cr("printStats", "ds.getWaitCount()", ds.getWaitCount());
//    }
}
