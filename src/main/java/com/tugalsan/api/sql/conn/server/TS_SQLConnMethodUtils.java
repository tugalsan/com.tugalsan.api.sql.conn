package com.tugalsan.api.sql.conn.server;

public class TS_SQLConnMethodUtils {

    public static boolean USE_MARIADB_DRIVER_AS_MYSQL_CONNECTOR = true;//change pom.xml too

    public static String get_METHOD_MYSQL_JAR_FILE_NAME_PREFIX() {
        //https://mariadb.com/downloads/connectors/connectors-data-access/java8-connector/
        return USE_MARIADB_DRIVER_AS_MYSQL_CONNECTOR ? "mariadb-java-client" : "mysql-connector-j-";
    }

    public static int METHOD_MYSQL() {
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

    public static String getDriver(TS_SQLConnConfig config) {
        if (config.method == METHOD_MYSQL()) {
            return USE_MARIADB_DRIVER_AS_MYSQL_CONNECTOR ? "org.mariadb.jdbc.Driver" : "com.mysql.cj.jdbc.Driver";//"com.mysql.jdbc.Driver";
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
        return "Unrecognized SQL method:" + config.method;
    }

    public static String getDriverProtocol(TS_SQLConnConfig config) {
        if (config.method == METHOD_MYSQL()) {
            return USE_MARIADB_DRIVER_AS_MYSQL_CONNECTOR ? "mariadb" : "mysql";
        }
        if (config.method == METHOD_ODBC()) {
            return "jdbc:odbc";//    final public static String config_link () "jdbc:odbc:" + databaseName;
        }
        if (config.method == METHOD_ORACLE()) {
            return "jdbc:oracle:thin";//    final public static String config_link () "jdbc:oracle:thin:@" + serverName + ":" + portNumber + ":" + databaseName;
        }
        if (config.method == METHOD_SQLSERVER()) {
            return "jdbc:jtds:sqlserver";//    final public static String config_link () "jdbc:jtds:sqlserver://" + serverName + ":" + portNumber + "/" + databaseName + ";instance=SQLEXPRESS";
        }
        if (config.method == METHOD_SMALLSQL()) {
            return "jdbc:smallsql";//    final public static String config_link () "jdbc:smallsql:" + databaseName + "?create=true";
        }
        return "Unrecognized SQL method:" + config.method;
    }
}
