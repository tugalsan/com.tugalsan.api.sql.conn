package com.tugalsan.api.sql.conn.server;

import com.tugalsan.api.os.server.TS_OsCpuUtils;
import java.io.Serializable;
import java.util.Objects;

public class TS_SQLConnConfig implements Serializable {

    public int method = TS_SQLConnMethodUtils.METHOD_MYSQL();
    public String dbName;
    public String dbIp = "localhost";
    public int dbPort = 3306;
    public String dbUser = "root";
    public String dbPassword = "";
    public boolean autoReconnect = true;
    public boolean useSSL = false;
    public boolean region_ist = true;
    public boolean charsetUTF8 = true;
    public boolean isPooled = true;
    public boolean pool_debug = false;
    public int pool_max_active_ms = 15000;
    public int pool_max_wait_ms = 30000;
    public int pool_concurrent = TS_OsCpuUtils.getProcessorCount() * 2;

    @Override
    public int hashCode() {
        var hash = 5;
        hash = 71 * hash + this.method;
        hash = 71 * hash + Objects.hashCode(this.dbName);
        hash = 71 * hash + Objects.hashCode(this.dbIp);
        hash = 71 * hash + this.dbPort;
        hash = 71 * hash + Objects.hashCode(this.dbUser);
        hash = 71 * hash + Objects.hashCode(this.dbPassword);
        hash = 71 * hash + (this.autoReconnect ? 1 : 0);
        hash = 71 * hash + (this.useSSL ? 1 : 0);
        hash = 71 * hash + (this.region_ist ? 1 : 0);
        hash = 71 * hash + (this.charsetUTF8 ? 1 : 0);
        hash = 71 * hash + (this.isPooled ? 1 : 0);
        hash = 71 * hash + (this.pool_debug ? 1 : 0);
        return hash;
    }

    @Deprecated//NEEDED FOR SERILIZE
    public TS_SQLConnConfig() {

    }

    private TS_SQLConnConfig(CharSequence dbName) {
        this.dbName = dbName == null ? null : dbName.toString();
    }

    public static TS_SQLConnConfig of(CharSequence dbName) {
        return new TS_SQLConnConfig(dbName);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" + "method=" + method + ", dbName=" + dbName + ", dbIp=" + dbIp + ", dbPort=" + dbPort + ", dbUser=" + dbUser + ", dbPassword=" + dbPassword + ", autoReconnect=" + autoReconnect + ", useSSL=" + useSSL + ", region_ist=" + region_ist + ", charsetUTF8=" + charsetUTF8 + ", isPooled=" + isPooled + ", pool_debug=" + pool_debug + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TS_SQLConnConfig)) {
            return false;
        }
        var o = (TS_SQLConnConfig) obj;
        return o.autoReconnect == autoReconnect
                && o.charsetUTF8 == charsetUTF8
                && o.isPooled == isPooled
                && o.pool_debug == pool_debug
                && o.region_ist == region_ist
                && o.useSSL == useSSL
                && Objects.equals(o.dbIp, dbIp)
                && Objects.equals(o.dbName, dbName)
                && Objects.equals(o.dbPassword, dbPassword)
                && o.dbPort == dbPort
                && Objects.equals(o.dbUser, dbUser)
                && o.method == method;
    }

    public TS_SQLConnConfig cloneItAs(CharSequence newDbName) {
        var cfg = new TS_SQLConnConfig(newDbName);
        cfg.autoReconnect = autoReconnect;
        cfg.charsetUTF8 = charsetUTF8;
        cfg.dbIp = dbIp;
        cfg.dbPassword = dbPassword;
        cfg.dbPort = dbPort;
        cfg.dbUser = dbUser;
        cfg.isPooled = isPooled;
        cfg.pool_debug = pool_debug;
        cfg.method = method;
        cfg.region_ist = region_ist;
        cfg.useSSL = useSSL;
        return cfg;
    }

    public TS_SQLConnConfig cloneIt() {
        return cloneItAs(dbName);
    }
}
