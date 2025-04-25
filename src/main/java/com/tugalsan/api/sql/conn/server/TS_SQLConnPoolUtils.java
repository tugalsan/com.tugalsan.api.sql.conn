package com.tugalsan.api.sql.conn.server;

import org.apache.tomcat.jdbc.pool.PoolProperties;

public class TS_SQLConnPoolUtils {

    private TS_SQLConnPoolUtils() {

    }

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
        pool.setInitialSize(10);//x < setMaxActive
        
        pool.setMaxWait(10000);
        pool.setRemoveAbandonedTimeout(60);//seconds
        pool.setMinEvictableIdleTimeMillis(30000);

        pool.setMinIdle(10);// minIdle < x < maxIdle & maxActive
        pool.setMaxIdle(100);//x < setMaxActive
        pool.setLogAbandoned(true);
        pool.setRemoveAbandoned(true);

        pool.setJdbcInterceptors(
                "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"
                + "org.apache.tomcat.jdbc.pool.interceptor.ResetAbandonedTimer;"
                + "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");

        return pool;
    }
}
