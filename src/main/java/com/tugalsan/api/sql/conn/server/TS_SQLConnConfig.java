package com.tugalsan.api.sql.conn.server;

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

    public TS_SQLConnConfig() {//NEEDED FOR SERILIZE

    }

    public TS_SQLConnConfig(CharSequence dbName) {
        this.dbName = dbName == null ? null : dbName.toString();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" + "method=" + method + ", dbName=" + dbName + ", dbIp=" + dbIp + ", dbPort=" + dbPort + ", dbUser=" + dbUser + ", dbPassword=" + dbPassword + ", autoReconnect=" + autoReconnect + ", useSSL=" + useSSL + ", region_ist=" + region_ist + ", charsetUTF8=" + charsetUTF8 + ", isPooled=" + isPooled + '}';
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
        cfg.method = method;
        cfg.region_ist = region_ist;
        cfg.useSSL = useSSL;
        return cfg;
    }

    public TS_SQLConnConfig cloneIt() {
        return cloneItAs(dbName);
    }
}
