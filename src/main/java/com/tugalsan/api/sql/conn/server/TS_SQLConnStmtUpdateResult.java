package com.tugalsan.api.sql.conn.server;

public record TS_SQLConnStmtUpdateResult(int affectedRowCount, Long autoId) {

    public static TS_SQLConnStmtUpdateResult of(int affectedRowCount, Long autoId) {
        return new TS_SQLConnStmtUpdateResult(affectedRowCount, autoId);
    }
}
