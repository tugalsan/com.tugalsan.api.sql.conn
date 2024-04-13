package com.tugalsan.api.sql.conn.server;

import com.tugalsan.api.file.obj.server.*;
import com.tugalsan.api.log.server.TS_Log;
import com.tugalsan.api.sql.col.typed.client.TGS_SQLColTypedUtils;
import com.tugalsan.api.string.server.*;
import com.tugalsan.api.union.client.TGS_UnionExcuse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.IntStream;

public class TS_SQLConnStmtUtils {

    final public static TS_Log d = TS_Log.of(TS_SQLConnStmtUtils.class);

    public static TGS_UnionExcuse<TS_SQLConnStmtUpdateResult> executeUpdate(PreparedStatement stmt) {
        var wrap = new Object() {
            int affectedRowCount;
            long autoId;
        };
        try {
            wrap.affectedRowCount = stmt.executeUpdate();
        } catch (SQLException ex) {
            return TGS_UnionExcuse.ofExcuse(ex);
        }
        try (var generatedKeys = stmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                wrap.autoId = generatedKeys.getLong(1);
            }
        } catch (SQLException ex) {
            return TGS_UnionExcuse.ofExcuse(ex);
        }
        return TGS_UnionExcuse.of(new TS_SQLConnStmtUpdateResult(wrap.affectedRowCount, wrap.autoId));
    }

    public static TGS_UnionExcuse<PreparedStatement> stmtUpdate(Connection con, CharSequence sql) {
        try {
            return TGS_UnionExcuse.of(con.prepareStatement(sql.toString(), PreparedStatement.RETURN_GENERATED_KEYS));
        } catch (SQLException ex) {
            return TGS_UnionExcuse.ofExcuse(ex);
        }
    }

    public static TGS_UnionExcuse<PreparedStatement> stmtQuery(Connection con, CharSequence sql) {
        var u_scrollingSupported = TS_SQLConnConUtils.scrollingSupported(con);
        if (u_scrollingSupported.isExcuse()) {
            return u_scrollingSupported.toExcuse();
        }
        try {
            return TGS_UnionExcuse.of(con.prepareStatement(sql.toString(), ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE));
        } catch (SQLException ex) {
            return TGS_UnionExcuse.ofExcuse(ex);
        }
    }

    public static TGS_UnionExcuse<Integer> fill(PreparedStatement fillStmt, List<String> colNames, List params, int index) {
        for (var i = 0; i < params.size(); i++) {
            var u = TS_SQLConnStmtUtils.fill(fillStmt, colNames.get(i), params.get(i), i);
            if (u.isExcuse()) {
                return u.toExcuse();
            }
        }
        return TGS_UnionExcuse.of(index + params.size());
    }

    public static TGS_UnionExcuse<Integer> fill(PreparedStatement fillStmt, String[] colNames, Object[] params, int index) {
        for (var i = 0; i < params.length; i++) {
            var u = TS_SQLConnStmtUtils.fill(fillStmt, colNames[i], params[i], i);
            if (u.isExcuse()) {
                return u.toExcuse();
            }
        }
        IntStream.range(index, params.length).forEachOrdered(i -> {

        });
        return TGS_UnionExcuse.of(index + params.length);
    }

    public static TGS_UnionExcuse<Integer> fill(PreparedStatement fillStmt, CharSequence colName, Object param, int index) {
        if (param instanceof byte[] val) {
            if (!TGS_SQLColTypedUtils.familyBytes(colName)) {
                TGS_UnionExcuse.ofExcuse(d.className, "fill", "param instanceof byte[] -> !TGS_SQLColTypedUtils.familyBytes(colName)");
            }
            d.ci("fill", index, "byte[]", "len", val.length);
            try {
                fillStmt.setBytes(index + 1, val);
            } catch (SQLException ex) {
                return TGS_UnionExcuse.ofExcuse(ex);
            }
            return TGS_UnionExcuse.of(index + 1);
        }
        if (param instanceof Boolean val) {
            if (!TGS_SQLColTypedUtils.familyLng(colName)) {
                TGS_UnionExcuse.ofExcuse(d.className, "fill", "param instanceof Boolean -> !TGS_SQLColTypedUtils.familyLng(colName)");
            }
            d.ci("fill", index, "bool", val);
            try {
                fillStmt.setLong(index + 1, val ? 1L : 0L);
            } catch (SQLException ex) {
                return TGS_UnionExcuse.ofExcuse(ex);
            }
            return TGS_UnionExcuse.of(index + 1);
        }
        if (param instanceof Short val) {
            if (!TGS_SQLColTypedUtils.familyLng(colName)) {
                TGS_UnionExcuse.ofExcuse(d.className, "fill", "param instanceof Short -> !TGS_SQLColTypedUtils.familyLng(colName)");
            }
            d.ci("fill", index, "Short", val);
            try {
                fillStmt.setLong(index + 1, val);
            } catch (SQLException ex) {
                return TGS_UnionExcuse.ofExcuse(ex);
            }
            return TGS_UnionExcuse.of(index + 1);
        }
        if (param instanceof Integer val) {
            if (!TGS_SQLColTypedUtils.familyLng(colName)) {
                TGS_UnionExcuse.ofExcuse(d.className, "fill", "param instanceof Integer -> !TGS_SQLColTypedUtils.familyLng(colName)");
            }
            d.ci("fill", index, "Integer", val);
            try {
                fillStmt.setLong(index + 1, val);
            } catch (SQLException ex) {
                return TGS_UnionExcuse.ofExcuse(ex);
            }
            return TGS_UnionExcuse.of(index + 1);
        }
        if (param instanceof Long val) {
            if (!TGS_SQLColTypedUtils.familyLng(colName)) {
                TGS_UnionExcuse.ofExcuse(d.className, "fill", "param instanceof Long -> !TGS_SQLColTypedUtils.familyLng(colName)");
            }
            d.ci("fill", index, "Long", val);
            try {
                fillStmt.setLong(index + 1, val);
            } catch (SQLException ex) {
                return TGS_UnionExcuse.ofExcuse(ex);
            }
            return TGS_UnionExcuse.of(index + 1);
        }
        if (param instanceof Object[] val) {
            if (!TGS_SQLColTypedUtils.typeBytes(colName) && !TGS_SQLColTypedUtils.typeBytesRow(colName)) {
                TGS_UnionExcuse.ofExcuse(d.className, "fill", "param instanceof Object[] -> !TGS_SQLColTypedUtils.typeBytes(colName) && !TGS_SQLColTypedUtils.typeBytesRow(colName)");
            }
            var opObj = TS_FileObjUtils.toBytes(val);
            if (opObj.isExcuse()) {
                TGS_UnionExcuse.ofExcuse(d.className, "fill", "param instanceof Object[] -> TS_FileObjUtils.toBytes(val).isEmpty");
            }
            var obj = opObj.value();
            d.ci("fill", index, "byte[].str", "len", obj.length);
            try {
                fillStmt.setBytes(index + 1, obj);
            } catch (SQLException ex) {
                return TGS_UnionExcuse.ofExcuse(ex);
            }
            return TGS_UnionExcuse.of(index + 1);
        }
        if (param instanceof CharSequence val) {
            var str = val.toString().replace("'", "\"");//JAVASCRIPT FIX
            if (TGS_SQLColTypedUtils.typeBytes(colName) || TGS_SQLColTypedUtils.typeBytesStr(colName)) {
                var obj = TS_StringUtils.toByte(str);
                d.ci("fill", index, "byte[].str", "len", obj.length);
                try {
                    fillStmt.setBytes(index + 1, obj);
                } catch (SQLException ex) {
                    return TGS_UnionExcuse.ofExcuse(ex);
                }
                return TGS_UnionExcuse.of(index + 1);
            }
            if (TGS_SQLColTypedUtils.familyStr(colName)) {
                d.ci("fill", index, "CharSequence", str);
                try {
                    fillStmt.setString(index + 1, str);
                } catch (SQLException ex) {
                    return TGS_UnionExcuse.ofExcuse(ex);
                }
                return TGS_UnionExcuse.of(index + 1);
            }
            TGS_UnionExcuse.ofExcuse(d.className, "fill", "CharSequence on not typeBytes or typeBytesStr col: " + colName);
        }
        return TGS_UnionExcuse.ofExcuse(d.className, "fill", "Uncoded type! [" + param + "]");
    }
}
