package com.tugalsan.api.sql.conn.server;

import com.tugalsan.api.log.server.*;
import java.sql.*;

public class TS_SQLConnPack implements AutoCloseable {

    final private static TS_Log d = TS_Log.of(TS_SQLConnWalkUtils.class.getSimpleName());

    public TS_SQLConnPack(TS_SQLConnAnchor anchor, Connection main, Connection proxy) {
        this.anchor = anchor;
        this.main = main;
        this.proxy = proxy;
    }
    private TS_SQLConnAnchor anchor;
    private Connection main;
    private Connection proxy;

    public TS_SQLConnAnchor anchor() {
        return anchor;
    }

    public Connection con() {
        return proxy == null ? main : proxy;
    }

    @Override
    public void close() {
        closeSilently("main", main);
        closeSilently("proxy", proxy);
    }

    private void closeSilently(CharSequence tag, Connection c) {
        try {
            if (c != null && !c.isClosed()) {
                c.close();
            }
        } catch (SQLException ex) {
            d.ce("close", tag, "error on close");
            ex.printStackTrace();
        }
    }
}
