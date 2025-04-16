package com.tugalsan.api.sql.conn.server;

import org.apache.tomcat.jdbc.pool.PoolProperties;

public class TS_SQLConnPoolUtils {
    
    private TS_SQLConnPoolUtils(){
        
    }

    public static PoolProperties create(TS_SQLConnConfig config) {
        var pool = new PoolProperties();

        pool.setUrl(TS_SQLConnURLUtils.create(config));
        pool.setDriverClassName(TS_SQLConnMethodUtils.getDriver(config));
        if (config.dbUser == null || config.dbUser.equals("") || config.dbPassword == null) {
        } else {
            pool.setUsername(config.dbUser);
            pool.setPassword(config.dbPassword);
        }
        var maxActive = 200;
        pool.setMaxActive(maxActive);
        pool.setMaxIdle(maxActive / 10);
        pool.setInitialSize(maxActive / 10);
        pool.setValidationInterval(30000);
        pool.setTimeBetweenEvictionRunsMillis(60000);
        pool.setMaxWait(10000);
        pool.setMinEvictableIdleTimeMillis(30000);
        pool.setMinIdle(10);
        pool.setRemoveAbandoned(true);
        pool.setRemoveAbandonedTimeout(600);
        pool.setFairQueue(true);
        pool.setLogAbandoned(true);
        pool.setJmxEnabled(true);
        pool.setTestOnBorrow(true);
        pool.setTestOnConnect(true);
        pool.setTestWhileIdle(true);
        pool.setTestOnReturn(false);
        pool.setTestWhileIdle(true);
        pool.setValidationQuery("SELECT 1");
        pool.setJdbcInterceptors(
                "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"
                + "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer;"
                + "org.apache.tomcat.jdbc.pool.interceptor.ResetAbandonedTimer");
        return pool;
    }
}
