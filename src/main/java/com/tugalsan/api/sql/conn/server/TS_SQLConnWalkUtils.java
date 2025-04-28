package com.tugalsan.api.sql.conn.server;

import com.tugalsan.api.function.client.maythrowexceptions.checked.TGS_FuncMTCUtils;
import com.tugalsan.api.function.client.maythrowexceptions.unchecked.TGS_FuncMTU_In1;
import com.tugalsan.api.log.server.TS_Log;
import com.tugalsan.api.tuple.client.TGS_Tuple1;
import com.tugalsan.api.sql.resultset.server.TS_SQLResultSet;

import java.sql.PreparedStatement;

public class TS_SQLConnWalkUtils {

    private TS_SQLConnWalkUtils() {

    }

    final private static TS_Log d = TS_Log.of(TS_SQLConnWalkUtils.class);

    public static boolean active(TS_SQLConnAnchor anchor) {
        TGS_Tuple1<Boolean> result = new TGS_Tuple1(false);
        var sqlStmt = "SELECT 'Hello world'  FROM DUAL";
        TGS_FuncMTCUtils.run(() -> {
            TS_SQLConnWalkUtils.stmtQuery(anchor, sqlStmt, stmt -> {
                TGS_FuncMTCUtils.run(() -> {
                    try (var resultSet = stmt.executeQuery();) {
                        var rs = new TS_SQLResultSet(resultSet);
                        var val = rs.str.get(0, 0);
                        d.ci("active", val);
                        result.value0 = true;
                    }
                }, e -> {
                    d.ce("active", "Is database driver loaded? Try restart!", e.getMessage());
                });
            });
        }, e -> {
            d.ce("active", "Is database online!", e.getMessage());
        });
        return result.value0;
    }

    private static void stmtQuery(TS_SQLConnAnchor anchor, CharSequence sql, TGS_FuncMTU_In1<PreparedStatement> stmt) {
        anchor.conRatedLimited(con -> {
            TGS_FuncMTCUtils.run(() -> {
                try (var stmt0 = TS_SQLConnStmtUtils.stmtQuery(con, sql);) {
                    stmt.run(stmt0);
                }
            });
        });
    }

    private static void stmtUpdate(TS_SQLConnAnchor anchor, CharSequence sql, TGS_FuncMTU_In1<PreparedStatement> stmt) {
        anchor.conRatedLimited(con -> {
            TGS_FuncMTCUtils.run(() -> {
                try (var stmt0 = TS_SQLConnStmtUtils.stmtUpdate(con, sql);) {
                    stmt.run(stmt0);
                }
            });
        });
    }

    public static void query(TS_SQLConnAnchor anchor, CharSequence sqlStmt, TGS_FuncMTU_In1<PreparedStatement> fillStmt, TGS_FuncMTU_In1<TS_SQLResultSet> rs) {
        if (d.infoEnable) {
            var sqlMsg = sqlStmt.toString().startsWith("SELECT * FROM MESSAGE");
            var sqlDom = sqlStmt.toString().startsWith("SELECT * FROM domain");
            if (!sqlMsg && !sqlDom) {
                d.ci("query", "sqlStmt", sqlStmt);
            }
        }
        TS_SQLConnWalkUtils.stmtQuery(anchor, sqlStmt, stmt -> {
            fillStmt.run(stmt);
            TGS_FuncMTCUtils.run(() -> {
                try (var resultSet = stmt.executeQuery();) {
                    var rso = new TS_SQLResultSet(resultSet);
                    rs.run(rso);
                }
            });
        });
    }

    public static TS_SQLConnStmtUpdateResult update(TS_SQLConnAnchor anchor, CharSequence sqlStmt, TGS_FuncMTU_In1<PreparedStatement> fillStmt) {
        d.ci("update", "sqlStmt", sqlStmt);
        TGS_Tuple1<TS_SQLConnStmtUpdateResult> pack = TGS_Tuple1.of();
        TS_SQLConnWalkUtils.stmtUpdate(anchor, sqlStmt, stmt -> {
            TGS_FuncMTCUtils.run(() -> {
                fillStmt.run(stmt);
                pack.value0 = TS_SQLConnStmtUtils.executeUpdate(stmt);
            });
        });
        return pack.value0;
    }
}
