package com.tugalsan.api.sql.conn.server;

public class TS_SQLConnMethodUtils {

    private TS_SQLConnMethodUtils() {

    }

    public static int METHOD_MARIADB() {
        return 0;
    }

    public static int METHOD_ODBC() {
        return 1;
    }

    public static int METHOD_ORACLE() {
        return 2;
    }

    public static int METHOD_SQLSERVER() {
        return 3;
    }

    public static int METHOD_SMALLSQL() {
        return 4;
    }

    public static int METHOD_MYSQL() {
        return 5;
    }

    public static String getJarName(TS_SQLConnConfig config) {
        if (config.method == METHOD_MARIADB()) {
            return "mariadb-java-client";
        }
        if (config.method == METHOD_ODBC()) {
            return "Unsupported SQL method:" + config.method;
        }
        if (config.method == METHOD_ORACLE()) {
            return "Unsupported SQL method:" + config.method;
        }
        if (config.method == METHOD_SQLSERVER()) {
            return "Unsupported SQL method:" + config.method;
        }
        if (config.method == METHOD_SMALLSQL()) {
            return "Unsupported SQL method:" + config.method;
        }
        if (config.method == METHOD_MYSQL()) {
            return "mysql-connector-j-";
        }
        return "Unrecognized SQL method:" + config.method;
    }

    public static String getDriver(TS_SQLConnConfig config) {
        if (config.method == METHOD_MARIADB()) {
            return "org.mariadb.jdbc.Driver";
        }
        if (config.method == METHOD_ODBC()) {
            return "sun.jdbc.odbc.JdbcOdbcDriver";
        }
        if (config.method == METHOD_ORACLE()) {
            return "oracle.jdbc.driver.OracleDriver";
        }
        if (config.method == METHOD_SQLSERVER()) {
            return "net.sourceforge.jtds.jdbc.Driver";
        }
        if (config.method == METHOD_SMALLSQL()) {
            return "smallsql.database.SSDriver";
        }
        if (config.method == METHOD_MYSQL()) {
            return "com.mysql.cj.jdbc.Driver";//"com.mysql.jdbc.Driver";
        }
        return "Unrecognized SQL method:" + config.method;
    }

    public static String getDriverProtocol(TS_SQLConnConfig config) {
        if (config.method == METHOD_MARIADB()) {
            return "mariadb";
        }
        if (config.method == METHOD_ODBC()) {
            return "jdbc:odbc";//"jdbc:odbc:" + databaseName;
        }
        if (config.method == METHOD_ORACLE()) {
            return "jdbc:oracle:thin";//"jdbc:oracle:thin:@" + serverName + ":" + portNumber + ":" + databaseName;
        }
        if (config.method == METHOD_SQLSERVER()) {
            return "jdbc:jtds:sqlserver";//"jdbc:jtds:sqlserver://" + serverName + ":" + portNumber + "/" + databaseName + ";instance=SQLEXPRESS";
        }
        if (config.method == METHOD_SMALLSQL()) {
            return "jdbc:smallsql";//"jdbc:smallsql:" + databaseName + "?create=true";
        }
        if (config.method == METHOD_MYSQL()) {
            return "mysql";
        }
        return "Unrecognized SQL method:" + config.method;
    }
}
