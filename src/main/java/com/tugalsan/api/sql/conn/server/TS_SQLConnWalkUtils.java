package com.tugalsan.api.sql.conn.server;

import com.tugalsan.api.executable.client.TGS_ExecutableType1;
import com.tugalsan.api.log.server.TS_Log;
import com.tugalsan.api.pack.client.TGS_Pack1;
import com.tugalsan.api.sql.resultset.server.TS_SQLResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class TS_SQLConnWalkUtils {

    final private static TS_Log d = TS_Log.of(TS_SQLConnWalkUtils.class.getSimpleName());

    public static void con(TS_SQLConnAnchor anchor, TGS_ExecutableType1<Connection> con) {
        try ( var conPack = TS_SQLConnConUtils.conPack(anchor);) {
            d.ci("con", anchor.config.dbName);
            con.execute(conPack.con());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean active(TS_SQLConnAnchor anchor) {
        TGS_Pack1<Boolean> result = new TGS_Pack1(false);
        var sqlStmt = "SELECT 'Hello world'  FROM DUAL";
        TS_SQLConnWalkUtils.stmt(anchor, sqlStmt, stmt -> {
            try ( var resultSet = stmt.executeQuery();) {
                var rs = new TS_SQLResultSet(resultSet);
                var val = rs.str.get(0, 0);
                d.ci("active", val);
                result.value0 = true;
            } catch (Exception e) {
                d.ce("active", "Is database driver loaded? Try restart!", e.getMessage());
            }
        });
        return result.value0;
    }

    private static void stmt(TS_SQLConnAnchor anchor, CharSequence sql, TGS_ExecutableType1<PreparedStatement> stmt) {
        con(anchor, con -> {
            try ( var stmt0 = TS_SQLConnStmtUtils.stmt(con, sql);) {
                stmt.execute(stmt0);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
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
        TS_SQLConnWalkUtils.stmt(anchor, sqlStmt, stmt -> {
            fillStmt.execute(stmt);
            try ( var resultSet = stmt.executeQuery();) {
                var rso = new TS_SQLResultSet(resultSet);
                rs.execute(rso);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static int update(TS_SQLConnAnchor anchor, CharSequence sqlStmt, TGS_ExecutableType1<PreparedStatement> fillStmt) {
        d.ci("update", "sqlStmt", sqlStmt);
        TGS_Pack1<Integer> pack = new TGS_Pack1(0);
        TS_SQLConnWalkUtils.stmt(anchor, sqlStmt, stmt -> {
            try {
                fillStmt.execute(stmt);
                pack.value0 = stmt.executeUpdate();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        return pack.value0;
    }
}
