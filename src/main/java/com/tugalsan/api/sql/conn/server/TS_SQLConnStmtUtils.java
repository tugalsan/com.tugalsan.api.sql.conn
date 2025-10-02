package com.tugalsan.api.sql.conn.server;

import module com.tugalsan.api.file.obj;
import module com.tugalsan.api.function;
import module com.tugalsan.api.log;
import module com.tugalsan.api.sql.col.typed;
import module com.tugalsan.api.string;
import module java.sql;
import java.util.*;
import java.util.stream.*;

public class TS_SQLConnStmtUtils {

    private TS_SQLConnStmtUtils() {

    }

    final public static TS_Log d = TS_Log.of(TS_SQLConnStmtUtils.class);

    public static TS_SQLConnStmtUpdateResult executeUpdate(PreparedStatement stmt) {
        var bag = TS_SQLConnStmtUpdateResult.of(0, null);
        TGS_FuncMTCUtils.run(() -> {
            bag.affectedRowCount = stmt.executeUpdate();
            try (var generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    bag.autoId = generatedKeys.getLong(1);
                }
            }
        }, e -> {
            TGS_FuncMTCUtils.run(() -> {
                d.ce("executeUpdate", "sql", stmt.getResultSet().getStatement());
            }, TGS_FuncMTU_In1.empty);
            d.ct("executeUpdate", e);
            TGS_FuncUtils.thrw(e);
        });
        return bag;
    }

    public static PreparedStatement stmtUpdate(Connection con, CharSequence sql) {
        return TGS_FuncMTCUtils.call(() -> {
            return con.prepareStatement(sql.toString(), PreparedStatement.RETURN_GENERATED_KEYS);
        }, e -> {
            TGS_FuncMTCUtils.run(() -> {
                d.ce("stmtUpdate", "sql", sql);
            }, TGS_FuncMTU_In1.empty);
            d.ct("stmtUpdate", e);
            return TGS_FuncUtils.thrw(e);
        });
    }

    public static PreparedStatement stmtQuery(Connection con, CharSequence sql) {
        return TGS_FuncMTCUtils.call(() -> {
            var scrollingSupported = TGS_FuncMTCUtils.call(() -> con.getMetaData().supportsResultSetType(ResultSet.TYPE_SCROLL_INSENSITIVE));
            if (!scrollingSupported) {
                TGS_FuncMTUUtils.thrw(d.className(), "stmtQuery", "!TS_SQLConnConUtils.scrollingSupported(con)");
            }
            return con.prepareStatement(sql.toString(), ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        }, e -> {
            TGS_FuncMTCUtils.run(() -> {
                d.ce("stmtQuery", "sql", sql);
            }, TGS_FuncMTU_In1.empty);
            d.ct("stmtQuery", e);
            return TGS_FuncUtils.thrw(e);
        });
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
        return TGS_FuncMTCUtils.call(() -> {
            if (param instanceof byte[] val) {
                if (!TGS_SQLColTypedUtils.familyBytes(colName)) {
                    TGS_FuncMTUUtils.thrw(d.className(), "fill", "param instanceof byte[] -> !TGS_SQLColTypedUtils.familyBytes(" + colName + ")");
                }
                d.ci("fill", index, "byte[]", "len", val.length);
                fillStmt.setBytes(index + 1, val);
                return index + 1;
            }
            if (param instanceof Boolean val) {
                if (!TGS_SQLColTypedUtils.familyLng(colName)) {
                    TGS_FuncMTUUtils.thrw(d.className(), "fill", "param instanceof Boolean -> !TGS_SQLColTypedUtils.familyLng(" + colName + ")");
                }
                d.ci("fill", index, "bool", val);
                fillStmt.setLong(index + 1, val ? 1L : 0L);
                return index + 1;
            }
            if (param instanceof Short val) {
                if (!TGS_SQLColTypedUtils.familyLng(colName)) {
                    TGS_FuncMTUUtils.thrw(d.className(), "fill", "param instanceof Short -> !TGS_SQLColTypedUtils.familyLng(" + colName + ")");
                }
                d.ci("fill", index, "Short", val);
                fillStmt.setLong(index + 1, val);
                return index + 1;
            }
            if (param instanceof Integer val) {
                if (!TGS_SQLColTypedUtils.familyLng(colName)) {
                    TGS_FuncMTUUtils.thrw(d.className(), "fill", "param instanceof Integer -> !TGS_SQLColTypedUtils.familyLng(" + colName + ")");
                }
                d.ci("fill", index, "Integer", val);
                fillStmt.setLong(index + 1, val);
                return index + 1;
            }
            if (param instanceof Long val) {
                if (!TGS_SQLColTypedUtils.familyLng(colName)) {
                    TGS_FuncMTUUtils.thrw(d.className(), "fill", "param instanceof Long -> !TGS_SQLColTypedUtils.familyLng(" + colName + ")");
                }
                d.ci("fill", index, "Long", val);
                fillStmt.setLong(index + 1, val);
                return index + 1;
            }
            if (param instanceof Object[] val) {
                if (!TGS_SQLColTypedUtils.typeBytes(colName) && !TGS_SQLColTypedUtils.typeBytesRow(colName)) {
                    TGS_FuncMTUUtils.thrw(d.className(), "fill", "param instanceof Object[] -> !TGS_SQLColTypedUtils.typeBytes(" + colName + ") && !TGS_SQLColTypedUtils.typeBytesRow(" + colName + ")");
                }
                var opObj = TS_FileObjUtils.toBytes(val);
                if (opObj.isExcuse()) {
                    TGS_FuncMTUUtils.thrw(d.className(), "fill", "param instanceof Object[] -> TS_FileObjUtils.toBytes(val).isEmpty:" + opObj.excuse().getMessage());
                }
                var obj = opObj.value();
                d.ci("fill", index, "byte[].str", "len", obj.length);
                fillStmt.setBytes(index + 1, obj);
                return index + 1;
            }
            if (param instanceof CharSequence val) {
                var str = val.toString().replace("'", "\"");//JAVASCRIPT FIX
                if (TGS_SQLColTypedUtils.typeBytes(colName) || TGS_SQLColTypedUtils.typeBytesStr(colName)) {
                    var obj = TGS_StringUtils.jre().toByte(str);
                    d.ci("fill", index, "byte[].str", "len", obj.length);
                    fillStmt.setBytes(index + 1, obj);
                    return index + 1;
                }
                if (TGS_SQLColTypedUtils.familyStr(colName)) {
                    d.ci("fill", index, "CharSequence", str);
                    fillStmt.setString(index + 1, str);
                    return index + 1;
                }
                TGS_FuncMTUUtils.thrw(d.className(), "fill", "CharSequence on not typeBytes or typeBytesStr col: " + colName);
            }
            return TGS_FuncMTUUtils.thrw(d.className(), "fill", "Uncoded type! [" + param + "]");
        }, e -> {
            TGS_FuncMTCUtils.run(() -> {
                d.ce("fill", "sql", fillStmt.getResultSet().getStatement());
            }, TGS_FuncMTU_In1.empty);
            d.ct("fill", e);
            return TGS_FuncUtils.thrw(e);
        });
    }
}
