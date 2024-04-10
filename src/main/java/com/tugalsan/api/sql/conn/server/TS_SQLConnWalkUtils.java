package com.tugalsan.api.sql.conn.server;

import com.tugalsan.api.runnable.client.TGS_RunnableType1;
import com.tugalsan.api.log.server.TS_Log;
import com.tugalsan.api.sql.resultset.server.TS_SQLResultSet;
import com.tugalsan.api.union.client.TGS_UnionExcuse;
import com.tugalsan.api.union.client.TGS_UnionExcuseVoid;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TS_SQLConnWalkUtils {

    final private static TS_Log d = TS_Log.of(TS_SQLConnWalkUtils.class);

    public static TGS_UnionExcuseVoid con(TS_SQLConnAnchor anchor, TGS_RunnableType1<Connection> con) {
        var u_conPack = TS_SQLConnConUtils.conPack(anchor);
        if (u_conPack.isExcuse()) {
            return u_conPack.toExcuseVoid();
        }
        try (var conPack = u_conPack.value()) {
            d.ci("con", anchor.config.dbName);
            con.run(conPack.con());
            return TGS_UnionExcuseVoid.ofVoid();
        } catch (SQLException ex) {
            return TGS_UnionExcuseVoid.ofExcuse(ex);
        }
    }

    public static TGS_UnionExcuseVoid active(TS_SQLConnAnchor anchor) {
        var wrap = new Object() {
            TGS_UnionExcuseVoid result = null;
        };
        var sqlStmt = "SELECT 'Hello world'  FROM DUAL";
        TS_SQLConnWalkUtils.stmtQuery(anchor, sqlStmt, stmt -> {
            try (var resultSet = stmt.executeQuery();) {
                var rs = new TS_SQLResultSet(resultSet);
                var u_val = rs.str.get(0, 0);
                if (u_val.isExcuse()) {
                    wrap.result = u_val.toExcuseVoid();
                }
                d.ci("active", u_val.value());
                wrap.result = TGS_UnionExcuseVoid.ofVoid();
            } catch (SQLException ex) {
                wrap.result = TGS_UnionExcuseVoid.ofExcuse(ex);
            }
        });
        return wrap.result;
    }

    private static TGS_UnionExcuseVoid stmtQuery(TS_SQLConnAnchor anchor, CharSequence sql, TGS_RunnableType1<PreparedStatement> stmt) {
        var wrap = new Object() {
            TGS_UnionExcuseVoid result = null;
        };
        var u_con = con(anchor, con -> {
            var u = TS_SQLConnStmtUtils.stmtQuery(con, sql);
            if (u.isExcuse()) {
                wrap.result = u.toExcuseVoid();
                return;
            }
            try (var stmt0 = u.value()) {
                stmt.run(stmt0);
                wrap.result = TGS_UnionExcuseVoid.ofVoid();
            } catch (SQLException e) {
                wrap.result = TGS_UnionExcuseVoid.ofExcuse(e);
            }
        });
        if (u_con.isExcuse()) {
            return u_con;
        }
        return wrap.result;
    }

    private static TGS_UnionExcuseVoid stmtUpdate(TS_SQLConnAnchor anchor, CharSequence sql, TGS_RunnableType1<PreparedStatement> stmt) {
        var wrap = new Object() {
            TGS_UnionExcuseVoid result = null;
        };
        var u_con = con(anchor, con -> {
            var u = TS_SQLConnStmtUtils.stmtUpdate(con, sql);
            if (u.isExcuse()) {
                wrap.result = u.toExcuseVoid();
                return;
            }
            try (var stmt0 = u.value()) {
                stmt.run(stmt0);
                wrap.result = TGS_UnionExcuseVoid.ofVoid();
            } catch (SQLException e) {
                wrap.result = TGS_UnionExcuseVoid.ofExcuse(e);
            }
        });
        if (u_con.isExcuse()) {
            return u_con;
        }
        return wrap.result;
    }

    public static TGS_UnionExcuseVoid query(TS_SQLConnAnchor anchor, CharSequence sqlStmt, TGS_RunnableType1<PreparedStatement> fillStmt, TGS_RunnableType1<TS_SQLResultSet> rs) {
        var wrap = new Object() {
            TGS_UnionExcuseVoid result = null;
        };
        var u_con = TS_SQLConnWalkUtils.stmtQuery(anchor, sqlStmt, stmt -> {
            fillStmt.run(stmt);
            try (var resultSet = stmt.executeQuery();) {
                var rso = new TS_SQLResultSet(resultSet);
                rs.run(rso);
                wrap.result = TGS_UnionExcuseVoid.ofVoid();
            } catch (SQLException e) {
                wrap.result = TGS_UnionExcuseVoid.ofExcuse(e);
            }
        });
        if (u_con.isExcuse()) {
            return u_con;
        }
        return wrap.result;
    }

    public static TGS_UnionExcuse<TS_SQLConnStmtUpdateResult> update(TS_SQLConnAnchor anchor, CharSequence sqlStmt, TGS_RunnableType1<PreparedStatement> fillStmt) {
        d.ci("update", "sqlStmt", sqlStmt);
        var wrap = new Object() {
            TGS_UnionExcuse<TS_SQLConnStmtUpdateResult> result = null;
        };
        var u_con = TS_SQLConnWalkUtils.stmtUpdate(anchor, sqlStmt, stmt -> {
            fillStmt.run(stmt);
            wrap.result = TS_SQLConnStmtUtils.executeUpdate(stmt);
        });
        if (u_con.isExcuse()) {
            return u_con.toExcuse();
        }
        return wrap.result;
    }
}
