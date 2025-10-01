package com.tugalsan.api.sql.conn.server.core;

import module com.tugalsan.api.function;
import module com.tugalsan.api.log;
import module com.tugalsan.api.profile;
import module com.tugalsan.api.sql.conn;
import module com.tugalsan.api.union;
import module java.sql;

public class TS_SQLConnCoreNewConnection implements AutoCloseable {

    final private static TS_Log d = TS_Log.of(TS_SQLConnWalkUtils.class);

    final public static TGS_UnionExcuse<TS_SQLConnCoreNewConnection> of(TS_SQLConnAnchor anchor) {
        return TGS_FuncMTCUtils.call(() -> {
            var u_main_con = TGS_FuncMTCUtils.<TGS_UnionExcuse<Connection>>call(() -> {
                Class.forName(TS_SQLConnMethodUtils.getDriver(anchor.config)).getConstructor().newInstance();
                return TGS_UnionExcuse.of(DriverManager.getConnection(anchor.url(), anchor.properties()));
            }, e -> TGS_UnionExcuse.ofExcuse(e));
            if (u_main_con.isExcuse()) {
                return u_main_con.toExcuse();
            }
            var u_proxy_con = TS_ProfileMelodyUtils.createProxy(u_main_con.value());
            if (u_proxy_con.isExcuse()) {
                return u_proxy_con.toExcuse();
            }
            var newConPack = new TS_SQLConnCoreNewConnection(
                    anchor,
                    u_main_con.value(),
                    u_proxy_con.value()
            );
            return TGS_UnionExcuse.of(newConPack);
        }, e -> TGS_UnionExcuse.ofExcuse(e));
    }

    private TS_SQLConnCoreNewConnection(TS_SQLConnAnchor anchor, Connection main, Connection proxy) {
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
