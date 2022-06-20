package com.tugalsan.api.sql.conn.server;

import com.tugalsan.api.file.obj.server.*;
import com.tugalsan.api.log.server.TS_Log;
import com.tugalsan.api.sql.col.typed.client.TGS_SQLColTypedUtils;
import com.tugalsan.api.string.server.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.stream.IntStream;

public class TS_SQLConnStmtUtils {

    final public static TS_Log d = TS_Log.of(TS_SQLConnStmtUtils.class.getSimpleName());

    public static PreparedStatement stmt(Connection con, CharSequence sql) {
        try {
            if (TS_SQLConnConUtils.scrollingSupported(con)) {
                return con.prepareStatement(sql.toString(), ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            } else {
                throw new RuntimeException(TS_SQLConnConUtils.class.getSimpleName() + ".of.scrollingSupported = false");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static int fill(PreparedStatement fillStmt, List<String> colNames, List params, int index) {
        IntStream.range(index, params.size()).forEachOrdered(i -> {
            TS_SQLConnStmtUtils.fill(fillStmt, colNames.get(i), params.get(i), i);
        });
        return index + params.size();
    }

    public static int fill(PreparedStatement fillStmt, String[] colNames, Object[] params, int index) {
        IntStream.range(index, params.length).forEachOrdered(i -> {
            TS_SQLConnStmtUtils.fill(fillStmt, colNames[i], params[i], i);
        });
        return index + params.length;
    }

    public static int fill(PreparedStatement fillStmt, CharSequence colName, Object param, int index) {
        try {
            if (param instanceof byte[]) {
                if (!TGS_SQLColTypedUtils.familyBytes(colName)) {
                    throw new RuntimeException("byte[] on not familyBytes col: " + colName);
                }
                var val = (byte[]) param;
                d.ci("fill", index, "byte[]", "len", val.length);
                fillStmt.setBytes(index + 1, val);
                return index + 1;
            }
            if (param instanceof Boolean) {
                if (!TGS_SQLColTypedUtils.familyLng(colName)) {
                    throw new RuntimeException("Boolean on not familyLng col: " + colName);
                }
                var val = (Boolean) param;
                d.ci("fill", index, "bool", val);
                fillStmt.setLong(index + 1, val ? 1L : 0L);
                return index + 1;
            }
            if (param instanceof Short) {
                if (!TGS_SQLColTypedUtils.familyLng(colName)) {
                    throw new RuntimeException("Short on not familyLng col: " + colName);
                }
                var val = (Short) param;
                d.ci("fill", index, "Short", val);
                fillStmt.setLong(index + 1, val);
                return index + 1;
            }
            if (param instanceof Integer) {
                if (!TGS_SQLColTypedUtils.familyLng(colName)) {
                    throw new RuntimeException("Integer on not familyLng col: " + colName);
                }
                var val = (Integer) param;
                d.ci("fill", index, "Integer", val);
                fillStmt.setLong(index + 1, val);
                return index + 1;
            }
            if (param instanceof Long) {
                if (!TGS_SQLColTypedUtils.familyLng(colName)) {
                    throw new RuntimeException("Long on not familyLng col: " + colName);
                }
                var val = (Long) param;
                d.ci("fill", index, "Long", val);
                fillStmt.setLong(index + 1, val);
                return index + 1;
            }
            if (param instanceof Object[]) {
                if (!TGS_SQLColTypedUtils.typeBytes(colName) && !TGS_SQLColTypedUtils.typeBytesRow(colName)) {
                    throw new RuntimeException("Object[] on not typeBytes or typeBytesRow col: " + colName);
                }
                var val = (Object[]) param;
                var obj = TS_FileObjUtils.toBytes(val);
                d.ci("fill", index, "byte[].str", "len", obj.length);
                fillStmt.setBytes(index + 1, obj);
                return index + 1;
            }
            if (param instanceof CharSequence) {
                var val = (CharSequence) param;
                var str = val.toString().replace("'", "\"");//JAVASCRIPT FIX
                if (TGS_SQLColTypedUtils.typeBytes(colName) || TGS_SQLColTypedUtils.typeBytesStr(colName)) {
                    var obj = TS_StringUtils.toByte(str);
                    d.ci("fill", index, "byte[].str", "len", obj.length);
                    fillStmt.setBytes(index + 1, obj);
                    return index + 1;
                }
                if (TGS_SQLColTypedUtils.familyStr(colName)) {
                    d.ci("fill", index, "CharSequence", str);
                    fillStmt.setString(index + 1, str);
                    return index + 1;
                }
                throw new RuntimeException("CharSequence on not typeBytes or typeBytesStr col: " + colName);
            }
            throw new RuntimeException(TS_SQLConnStmtUtils.class.getSimpleName() + ".fill-> Error: Uncoded type! [" + param + "]");
        } catch (Exception ex) {
            throw new RuntimeException(TS_SQLConnStmtUtils.class.getSimpleName() + ".fill-> Error: idx:" + index + " for " + ex.getMessage(), ex);
        }
    }
}
