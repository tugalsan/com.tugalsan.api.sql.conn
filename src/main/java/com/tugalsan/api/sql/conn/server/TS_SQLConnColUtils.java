package com.tugalsan.api.sql.conn.server;

import com.tugalsan.api.function.client.maythrowexceptions.unchecked.TGS_FuncMTUUtils;
import com.tugalsan.api.log.server.*;
import com.tugalsan.api.tuple.client.*;
import com.tugalsan.api.sql.col.typed.client.*;
import com.tugalsan.api.sql.sanitize.server.*;
import com.tugalsan.api.string.client.*;

import java.util.*;

public class TS_SQLConnColUtils {

    private TS_SQLConnColUtils() {

    }

    final private static TS_Log d = TS_Log.of(TS_SQLConnColUtils.class);

    public static List<String> names(TS_SQLConnAnchor anchor, CharSequence tableName) {
        TS_SQLSanitizeUtils.sanitize(tableName);
        var sqlStmt = TGS_StringUtils.cmn().concat(
                "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE table_name = '", tableName, "' AND table_schema= '", anchor.config.dbName, "' ORDER BY ORDINAL_POSITION"
        );
        d.ci("names", sqlStmt);
        TGS_Tuple1<List<String>> pack = new TGS_Tuple1();
        TS_SQLConnWalkUtils.query(anchor, sqlStmt, fillStmt -> {
        }, rs -> pack.value0 = rs.strArr.get(0));
        return pack.value0;
    }

    public static String creationType(TS_SQLConnAnchor anchor, TGS_SQLColTyped colType) {
        return creationType(anchor.config, colType);
    }

    public static String creationType(TS_SQLConnConfig config, TGS_SQLColTyped colType) {
        if (Objects.equals(config.method, TS_SQLConnMethodUtils.METHOD_MARIADB()) || Objects.equals(config.method, TS_SQLConnMethodUtils.METHOD_MYSQL())) {
            if (colType.familyLng()) {
                return "INTEGER NOT NULL";
            }
            if (colType.familyStr()) {
                return "VARCHAR(254) NOT NULL";
            }
            if (colType.familyBytes()) {
                return "LONGBLOB";
            }
            return TGS_FuncMTUUtils.thrw(d.className, "creationType(TS_SQLConnConfig config, TGS_SQLColTyped colType)", "Unrecognized SQL colType:" + colType);
        }
        if (Objects.equals(config.method, TS_SQLConnMethodUtils.METHOD_ODBC())) {
            if (colType.familyLng()) {
                return "INTEGER NOT NULL";
            }
            if (colType.familyStr()) {
                return "VARCHAR(254) NOT NULL";
            }
            if (colType.familyBytes()) {
                return "LONGBINARY";
            }
            return TGS_FuncMTUUtils.thrw(d.className, "creationType(TS_SQLConnConfig config, TGS_SQLColTyped colType)", "Unrecognized SQL colType:" + colType);
        }
        if (Objects.equals(config.method, TS_SQLConnMethodUtils.METHOD_ORACLE())) {
            if (colType.familyLng()) {
                return "INTEGER NOT NULL";
            }
            if (colType.familyStr()) {
                return "VARCHAR2(254) NOT NULL";
            }
            if (colType.familyBytes()) {
                return "LONGBLOB";
            }
            return TGS_FuncMTUUtils.thrw(d.className, "creationType(TS_SQLConnConfig config, TGS_SQLColTyped colType)", "Unrecognized SQL colType:" + colType);
        }
        if (Objects.equals(config.method, TS_SQLConnMethodUtils.METHOD_SQLSERVER())) {
            if (colType.familyLng()) {
                return "INT NOT NULL";
            }
            if (colType.familyStr()) {
                return "VARCHAR(254) NOT NULL";
            }
            if (colType.familyBytes()) {
                return "IMAGE"; //BINARY, VARBIMARY, IMAGE
            }
            return TGS_FuncMTUUtils.thrw(d.className, "creationType(TS_SQLConnConfig config, TGS_SQLColTyped colType)", "Unrecognized SQL colType:" + colType);
        }
        if (Objects.equals(config.method, TS_SQLConnMethodUtils.METHOD_SMALLSQL())) {
            //BIT, BOOLEAN, BINARY, VARBINARY, RAW, LONGVARBINARY, BLOB,
            //TINYINT, SMALLINT, INT, COUNTER, BIGINT, SMALLMONEY, MONEY,
            //DECIMAL, NUMERIC, REAL, FLOAT, DOUBLE, DATE, TIME, TIMESTAMP,
            //SMALLDATETIME, CHAR, NCHAR, VARCHAR, NVARCHAR, LONG, LONGNVARCHAR,
            //LONGVARCHAR, CLOB, NCLOB, UNIQUEIDENTIFIER, JAVA_OBJECT or SYSNAME
            if (colType.familyLng()) {
                return "INT NOT NULL";
            }
            if (colType.familyStr()) {
                return "VARCHAR(254) NOT NULL";
            }
            if (colType.familyBytes()) {
                return "LONGVARBINARY";
            }
            return TGS_FuncMTUUtils.thrw(d.className, "creationType(TS_SQLConnConfig config, TGS_SQLColTyped colType)", "Unrecognized SQL colType:" + colType);
        }
        return TGS_FuncMTUUtils.thrw(d.className, "creationType(TS_SQLConnConfig config, TGS_SQLColTyped colType)", "Unrecognized SQL method:" + config.method);
    }
}
