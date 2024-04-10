package com.tugalsan.api.sql.conn.server;

import com.tugalsan.api.log.server.*;
import java.sql.*;

public class TS_SQLConnPack implements AutoCloseable {

    final private static TS_Log d = TS_Log.of(TS_SQLConnWalkUtils.class);

    public TS_SQLConnPack(TS_SQLConnAnchor anchor, Connection main, Connection proxy) {
        this.anchor = anchor;
        this.main = main;
        this.proxy = proxy;
    }
    private final TS_SQLConnAnchor anchor;
    private final Connection main;
    private final Connection proxy;

    public TS_SQLConnAnchor anchor() {
        return anchor;
    }

    public Connection con() {
        return proxy == null ? main : proxy;
    }

    @Override
    public void close() throws SQLException {
        var e_main = closeIt(main);
        var e_proxy = closeIt(proxy);
        if (e_main != null) {
            throw e_main;
        }
        if (e_proxy != null) {
            throw e_proxy;
        }
    }

    private SQLException closeIt(Connection c) {
        try {
            if (c != null && !c.isClosed()) {
                c.close();
            }
            return null;
        } catch (SQLException ex) {
            return ex;
        }
    }
}
