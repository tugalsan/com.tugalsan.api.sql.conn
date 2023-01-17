package com.tugalsan.api.sql.conn.server;

public class TS_SQLConnStmtUpdateResult {

    public int affectedRowCount;
    public Long autoId;

    private TS_SQLConnStmtUpdateResult(int affectedRowCount, Long autoId) {
        this.affectedRowCount = affectedRowCount;
        this.autoId = autoId;
    }

    public static TS_SQLConnStmtUpdateResult of(int affectedRowCount, Long autoId) {
        return new TS_SQLConnStmtUpdateResult(affectedRowCount, autoId);
    }

    @Override
    public String toString() {
        return TS_SQLConnStmtUpdateResult.class.getSimpleName() + "{" + "affectedRowCount=" + affectedRowCount + ", autoId=" + autoId + '}';
    }
}
