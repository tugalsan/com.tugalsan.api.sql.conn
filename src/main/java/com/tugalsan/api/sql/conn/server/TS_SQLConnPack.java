package com.tugalsan.api.sql.conn.server;

import com.tugalsan.api.function.client.maythrowexceptions.checked.TGS_FuncMTCUtils;
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
    public void close() {
        closeSilently("main", main);
        closeSilently("proxy", proxy);
    }

    private void closeSilently(CharSequence tag, Connection c) {
        TGS_FuncMTCUtils.run(() -> {
            if (c != null && !c.isClosed()) {
                c.close();
            }
        }, e -> {
            d.ce("close", tag, "error on close");
            d.ct("close", e);
        });
    }
}
