package com.tugalsan.api.sql.conn.server;

import com.tugalsan.api.executable.client.TGS_ExecutableType1;
import com.tugalsan.api.log.server.TS_Log;
import com.tugalsan.api.pack.client.TGS_Pack1;
import com.tugalsan.api.sql.resultset.server.TS_SQLResultSet;
import com.tugalsan.api.unsafe.client.*;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class TS_SQLConnWalkUtils {

    final private static TS_Log d = TS_Log.of(TS_SQLConnWalkUtils.class);

    public static void con(TS_SQLConnAnchor anchor, TGS_ExecutableType1<Connection> con) {
        TGS_UnSafe.execute(() -> {
            try ( var conPack = TS_SQLConnConUtils.conPack(anchor);) {
                d.ci("con", anchor.config.dbName);
                con.execute(conPack.con());
            }
        });
    }

    public static boolean active(TS_SQLConnAnchor anchor) {
        TGS_Pack1<Boolean> result = new TGS_Pack1(false);
        var sqlStmt = "SELECT 'Hello world'  FROM DUAL";
        TS_SQLConnWalkUtils.stmtQuery(anchor, sqlStmt, stmt -> {
            TGS_UnSafe.execute(() -> {
                try ( var resultSet = stmt.executeQuery();) {
                    var rs = new TS_SQLResultSet(resultSet);
                    var val = rs.str.get(0, 0);
                    d.ci("active", val);
                    result.value0 = true;
                }
            }, exception -> {
                d.ce("active", "Is database driver loaded? Try restart!", exception.getMessage());
            });
        });
        return result.value0;
    }

    private static void stmtQuery(TS_SQLConnAnchor anchor, CharSequence sql, TGS_ExecutableType1<PreparedStatement> stmt) {
        con(anchor, con -> {
            TGS_UnSafe.execute(() -> {
                try ( var stmt0 = TS_SQLConnStmtUtils.stmtQuery(con, sql);) {
                    stmt.execute(stmt0);
                }
            });
        });
    }

    private static void stmtUpdate(TS_SQLConnAnchor anchor, CharSequence sql, TGS_ExecutableType1<PreparedStatement> stmt) {
        con(anchor, con -> {
            TGS_UnSafe.execute(() -> {
                try ( var stmt0 = TS_SQLConnStmtUtils.stmtUpdate(con, sql);) {
                    stmt.execute(stmt0);
                }
            });
        });
    }

    public static void query(TS_SQLConnAnchor anchor, CharSequence sqlStmt, TGS_ExecutableType1<PreparedStatement> fillStmt, TGS_ExecutableType1<TS_SQLResultSet> rs) {
        if (d.infoEnable) {
            var sqlMsg = sqlStmt.toString().startsWith("SELECT * FROM MESSAGE");
            var sqlDom = sqlStmt.toString().startsWith("SELECT * FROM domain");
            if (!sqlMsg && !sqlDom) {
                d.ci("query", "sqlStmt", sqlStmt);
            }
        }
        TS_SQLConnWalkUtils.stmtQuery(anchor, sqlStmt, stmt -> {
            fillStmt.execute(stmt);
            TGS_UnSafe.execute(() -> {
                try ( var resultSet = stmt.executeQuery();) {
                    var rso = new TS_SQLResultSet(resultSet);
                    rs.execute(rso);
                }
            });
        });
    }

    public static TS_SQLConnStmtUpdateResult update(TS_SQLConnAnchor anchor, CharSequence sqlStmt, TGS_ExecutableType1<PreparedStatement> fillStmt) {
        d.ci("update", "sqlStmt", sqlStmt);
        TGS_Pack1<TS_SQLConnStmtUpdateResult> pack = TGS_Pack1.of();
        TS_SQLConnWalkUtils.stmtUpdate(anchor, sqlStmt, stmt -> {
            TGS_UnSafe.execute(() -> {
                fillStmt.execute(stmt);
                pack.value0 = TS_SQLConnStmtUtils.executeUpdate(stmt);
            });
        });
        return pack.value0;
    }
}
