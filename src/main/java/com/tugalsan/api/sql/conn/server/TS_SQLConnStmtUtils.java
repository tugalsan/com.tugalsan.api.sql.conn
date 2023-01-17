package com.tugalsan.api.sql.conn.server;

import com.tugalsan.api.file.obj.server.*;
import com.tugalsan.api.log.server.TS_Log;
import com.tugalsan.api.pack.client.TGS_Pack2;
import com.tugalsan.api.sql.col.typed.client.TGS_SQLColTypedUtils;
import com.tugalsan.api.string.server.*;
import com.tugalsan.api.unsafe.client.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.stream.IntStream;

public class TS_SQLConnStmtUtils {

    final public static TS_Log d = TS_Log.of(TS_SQLConnStmtUtils.class);

    public static TS_SQLConnStmtUpdateResult executeUpdate(PreparedStatement stmt) {
        var bag = TS_SQLConnStmtUpdateResult.of(0, null);
        TGS_UnSafe.execute(() -> {
            bag.affectedRowCount = stmt.executeUpdate();
            try ( var generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    bag.autoId = generatedKeys.getLong(1);
                }
            }
        });
        return bag;
    }

    public static PreparedStatement stmt(Connection con, CharSequence sql) {
        return TGS_UnSafe.compile(() -> {
            if (!TS_SQLConnConUtils.scrollingSupported(con)) {
                TGS_UnSafe.catchMeIfUCan(d.className, "stmt", "!TS_SQLConnConUtils.scrollingSupported(con)");
            }
            return con.prepareStatement(sql.toString(), ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE, Statement.RETURN_GENERATED_KEYS);
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
        return TGS_UnSafe.compile(() -> {
            if (param instanceof byte[] val) {
                if (!TGS_SQLColTypedUtils.familyBytes(colName)) {
                    TGS_UnSafe.catchMeIfUCan(d.className, "fill", "param instanceof byte[] -> !TGS_SQLColTypedUtils.familyBytes(colName)");
                }
                d.ci("fill", index, "byte[]", "len", val.length);
                fillStmt.setBytes(index + 1, val);
                return index + 1;
            }
            if (param instanceof Boolean val) {
                if (!TGS_SQLColTypedUtils.familyLng(colName)) {
                    TGS_UnSafe.catchMeIfUCan(d.className, "fill", "param instanceof Boolean -> !TGS_SQLColTypedUtils.familyLng(colName)");
                }
                d.ci("fill", index, "bool", val);
                fillStmt.setLong(index + 1, val ? 1L : 0L);
                return index + 1;
            }
            if (param instanceof Short val) {
                if (!TGS_SQLColTypedUtils.familyLng(colName)) {
                    TGS_UnSafe.catchMeIfUCan(d.className, "fill", "param instanceof Short -> !TGS_SQLColTypedUtils.familyLng(colName)");
                }
                d.ci("fill", index, "Short", val);
                fillStmt.setLong(index + 1, val);
                return index + 1;
            }
            if (param instanceof Integer val) {
                if (!TGS_SQLColTypedUtils.familyLng(colName)) {
                    TGS_UnSafe.catchMeIfUCan(d.className, "fill", "param instanceof Integer -> !TGS_SQLColTypedUtils.familyLng(colName)");
                }
                d.ci("fill", index, "Integer", val);
                fillStmt.setLong(index + 1, val);
                return index + 1;
            }
            if (param instanceof Long val) {
                if (!TGS_SQLColTypedUtils.familyLng(colName)) {
                    TGS_UnSafe.catchMeIfUCan(d.className, "fill", "param instanceof Long -> !TGS_SQLColTypedUtils.familyLng(colName)");
                }
                d.ci("fill", index, "Long", val);
                fillStmt.setLong(index + 1, val);
                return index + 1;
            }
            if (param instanceof Object[] val) {
                if (!TGS_SQLColTypedUtils.typeBytes(colName) && !TGS_SQLColTypedUtils.typeBytesRow(colName)) {
                    TGS_UnSafe.catchMeIfUCan(d.className, "fill", "param instanceof Object[] -> !TGS_SQLColTypedUtils.typeBytes(colName) && !TGS_SQLColTypedUtils.typeBytesRow(colName)");
                }
                var obj = TS_FileObjUtils.toBytes(val);
                d.ci("fill", index, "byte[].str", "len", obj.length);
                fillStmt.setBytes(index + 1, obj);
                return index + 1;
            }
            if (param instanceof CharSequence val) {
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
                TGS_UnSafe.catchMeIfUCan(d.className, "fill", "CharSequence on not typeBytes or typeBytesStr col: " + colName);
            }
            return TGS_UnSafe.catchMeIfUCanReturns(d.className, "fill", "Uncoded type! [" + param + "]");
        });
    }
}
