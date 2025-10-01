package com.tugalsan.api.sql.conn.server;

import module com.tugalsan.api.function;
import module com.tugalsan.api.log;
import module com.tugalsan.api.tuple;
import module com.tugalsan.api.sql.resultset;
import module java.sql;

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

    @Deprecated //WARNING: CHECK TO SEE IF SQL IS SAFE!
    private static void stmtQuery(TS_SQLConnAnchor anchor, CharSequence sql, TGS_FuncMTU_In1<PreparedStatement> stmt) {
        anchor.use(con -> {
            TGS_FuncMTCUtils.run(() -> {
                try (var stmt0 = TS_SQLConnStmtUtils.stmtQuery(con, sql);) {
                    stmt.run(stmt0);
                }
            });
        });
    }

    @Deprecated //WARNING: CHECK TO SEE IF SQL IS SAFE!
    private static void stmtUpdate(TS_SQLConnAnchor anchor, CharSequence sql, TGS_FuncMTU_In1<PreparedStatement> stmt) {
        anchor.use(con -> {
            TGS_FuncMTCUtils.run(() -> {
                try (var stmt0 = TS_SQLConnStmtUtils.stmtUpdate(con, sql);) {
                    stmt.run(stmt0);
                }
            });
        });
    }

    @Deprecated //WARNING: CHECK TO SEE IF SQL IS SAFE!
    public static void query(TS_SQLConnAnchor anchor, CharSequence sqlStmt, TGS_FuncMTU_In1<PreparedStatement> fillStmt, TGS_FuncMTU_In1<TS_SQLResultSet> rs) {
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

    @Deprecated //WARNING: CHECK TO SEE IF SQL IS SAFE!
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
