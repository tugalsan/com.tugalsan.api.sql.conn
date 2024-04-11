package com.tugalsan.api.sql.conn.server;

import com.tugalsan.api.log.server.*;
import com.tugalsan.api.sql.col.typed.client.*;
import com.tugalsan.api.sql.sanitize.server.*;
import com.tugalsan.api.string.client.*;
import com.tugalsan.api.union.client.TGS_UnionExcuse;
import java.util.*;

public class TS_SQLConnColUtils {

    final private static TS_Log d = TS_Log.of(TS_SQLConnColUtils.class);

    public static TGS_UnionExcuse<List<String>> names(TS_SQLConnAnchor anchor, CharSequence tableName) {
        var wrap = new Object() {
            TGS_UnionExcuse<List<String>> u_rs_strArr_get = null;
        };
        TS_SQLSanitizeUtils.sanitize(tableName);
        var sqlStmt = TGS_StringUtils.concat(
                "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE table_name = '", tableName, "' AND table_schema= '", anchor.config.dbName, "' ORDER BY ORDINAL_POSITION"
        );
        d.ci("names", sqlStmt);
        var u_con = TS_SQLConnWalkUtils.query(anchor, sqlStmt, fillStmt -> {
        }, rs -> {
            wrap.u_rs_strArr_get = rs.strArr.get(0);
        });
        if (u_con.isExcuse()) {
            return u_con.toExcuse();
        }
        return wrap.u_rs_strArr_get;
    }

    public static TGS_UnionExcuse<String> creationType(TS_SQLConnAnchor anchor, TGS_SQLColTyped colType) {
        return creationType(anchor.config, colType);
    }

    public static TGS_UnionExcuse<String> creationType(TS_SQLConnConfig config, TGS_SQLColTyped colType) {
        if (Objects.equals(config.method, TS_SQLConnMethodUtils.METHOD_MYSQL())) {
            if (colType.familyLng()) {
                return TGS_UnionExcuse.of("INTEGER NOT NULL");
            }
            if (colType.familyStr()) {
                return TGS_UnionExcuse.of("VARCHAR(254) NOT NULL");
            }
            if (colType.familyBytes()) {
                return TGS_UnionExcuse.of("LONGBLOB");
            }
            return TGS_UnionExcuse.ofExcuse(d.className, "creationType(TS_SQLConnConfig config, TGS_SQLColTyped colType)", "Unrecognized SQL colType:" + colType);
        }
        if (Objects.equals(config.method, TS_SQLConnMethodUtils.METHOD_ODBC())) {
            if (colType.familyLng()) {
                return TGS_UnionExcuse.of("INTEGER NOT NULL");
            }
            if (colType.familyStr()) {
                return TGS_UnionExcuse.of("VARCHAR(254) NOT NULL");
            }
            if (colType.familyBytes()) {
                return TGS_UnionExcuse.of("LONGBINARY");
            }
            return TGS_UnionExcuse.ofExcuse(d.className, "creationType(TS_SQLConnConfig config, TGS_SQLColTyped colType)", "Unrecognized SQL colType:" + colType);
        }
        if (Objects.equals(config.method, TS_SQLConnMethodUtils.METHOD_ORACLE())) {
            if (colType.familyLng()) {
                return TGS_UnionExcuse.of("INTEGER NOT NULL");
            }
            if (colType.familyStr()) {
                return TGS_UnionExcuse.of("VARCHAR2(254) NOT NULL");
            }
            if (colType.familyBytes()) {
                return TGS_UnionExcuse.of("LONGBLOB");
            }
            return TGS_UnionExcuse.ofExcuse(d.className, "creationType(TS_SQLConnConfig config, TGS_SQLColTyped colType)", "Unrecognized SQL colType:" + colType);
        }
        if (Objects.equals(config.method, TS_SQLConnMethodUtils.METHOD_SQLSERVER())) {
            if (colType.familyLng()) {
                return TGS_UnionExcuse.of("INT NOT NULL");
            }
            if (colType.familyStr()) {
                return TGS_UnionExcuse.of("VARCHAR(254) NOT NULL");
            }
            if (colType.familyBytes()) {
                return TGS_UnionExcuse.of("IMAGE"); //BINARY, VARBIMARY, IMAGE
            }
            return TGS_UnionExcuse.ofExcuse(d.className, "creationType(TS_SQLConnConfig config, TGS_SQLColTyped colType)", "Unrecognized SQL colType:" + colType);
        }
        if (Objects.equals(config.method, TS_SQLConnMethodUtils.METHOD_SMALLSQL())) {
            //BIT, BOOLEAN, BINARY, VARBINARY, RAW, LONGVARBINARY, BLOB,
            //TINYINT, SMALLINT, INT, COUNTER, BIGINT, SMALLMONEY, MONEY,
            //DECIMAL, NUMERIC, REAL, FLOAT, DOUBLE, DATE, TIME, TIMESTAMP,
            //SMALLDATETIME, CHAR, NCHAR, VARCHAR, NVARCHAR, LONG, LONGNVARCHAR,
            //LONGVARCHAR, CLOB, NCLOB, UNIQUEIDENTIFIER, JAVA_OBJECT or SYSNAME
            if (colType.familyLng()) {
                return TGS_UnionExcuse.of("INT NOT NULL");
            }
            if (colType.familyStr()) {
                return TGS_UnionExcuse.of("VARCHAR(254) NOT NULL");
            }
            if (colType.familyBytes()) {
                return TGS_UnionExcuse.of("LONGVARBINARY");
            }
            return TGS_UnionExcuse.ofExcuse(d.className, "creationType(TS_SQLConnConfig config, TGS_SQLColTyped colType)", "Unrecognized SQL colType:" + colType);
        }
        return TGS_UnionExcuse.ofExcuse(d.className, "creationType(TS_SQLConnConfig config, TGS_SQLColTyped colType)", "Unrecognized SQL method:" + config.method);
    }
}
