package com.tugalsan.api.sql.conn.server;

public class TS_SQLConnStmtUpdatePack {

    public int affectedRowCount;
    public Long autoId;

    private TS_SQLConnStmtUpdatePack(int affectedRowCount, Long autoId) {
        this.affectedRowCount = affectedRowCount;
        this.autoId = autoId;
    }

    public static TS_SQLConnStmtUpdatePack of(int affectedRowCount, Long autoId) {
        return new TS_SQLConnStmtUpdatePack(affectedRowCount, autoId);
    }
}
