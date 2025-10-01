package com.tugalsan.api.sql.conn.server;

import java.io.*;
import java.util.*;

public class TS_SQLConnConfig implements Serializable {

    public int method = TS_SQLConnMethodUtils.METHOD_MARIADB();
    public String dbName;
    public String dbIp = "localhost";
    public int dbPort = 3306;
    public String dbUser = "root";
    public String dbPassword = "";
    public boolean autoReconnect = true;
    public boolean useSSL = false;
    public boolean region_ist = true;
    public boolean charsetUTF8 = true;
    public boolean useCacheIfPossible = false;

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + this.method;
        hash = 89 * hash + Objects.hashCode(this.dbName);
        hash = 89 * hash + Objects.hashCode(this.dbIp);
        hash = 89 * hash + this.dbPort;
        hash = 89 * hash + Objects.hashCode(this.dbUser);
        hash = 89 * hash + Objects.hashCode(this.dbPassword);
        hash = 89 * hash + (this.autoReconnect ? 1 : 0);
        hash = 89 * hash + (this.useSSL ? 1 : 0);
        hash = 89 * hash + (this.region_ist ? 1 : 0);
        hash = 89 * hash + (this.charsetUTF8 ? 1 : 0);
        hash = 89 * hash + (this.useCacheIfPossible ? 1 : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TS_SQLConnConfig other = (TS_SQLConnConfig) obj;
        if (this.method != other.method) {
            return false;
        }
        if (this.dbPort != other.dbPort) {
            return false;
        }
        if (this.autoReconnect != other.autoReconnect) {
            return false;
        }
        if (this.useSSL != other.useSSL) {
            return false;
        }
        if (this.region_ist != other.region_ist) {
            return false;
        }
        if (this.charsetUTF8 != other.charsetUTF8) {
            return false;
        }
        if (this.useCacheIfPossible != other.useCacheIfPossible) {
            return false;
        }
        if (!Objects.equals(this.dbName, other.dbName)) {
            return false;
        }
        if (!Objects.equals(this.dbIp, other.dbIp)) {
            return false;
        }
        if (!Objects.equals(this.dbUser, other.dbUser)) {
            return false;
        }
        return Objects.equals(this.dbPassword, other.dbPassword);
    }

    @Override
    public String toString() {
        return TS_SQLConnConfig.class.getSimpleName() + "{" + "method=" + method + ", dbName=" + dbName + ", dbIp=" + dbIp + ", dbPort=" + dbPort + ", dbUser=" + dbUser + ", dbPassword=" + dbPassword + ", autoReconnect=" + autoReconnect + ", useSSL=" + useSSL + ", region_ist=" + region_ist + ", charsetUTF8=" + charsetUTF8+ ", useCacheIfPossible=" + useCacheIfPossible + '}';
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

    public TS_SQLConnConfig cloneItAs(CharSequence newDbName) {
        var cfg = new TS_SQLConnConfig(newDbName);
        cfg.autoReconnect = autoReconnect;
        cfg.charsetUTF8 = charsetUTF8;
        cfg.dbIp = dbIp;
        cfg.dbPassword = dbPassword;
        cfg.dbPort = dbPort;
        cfg.dbUser = dbUser;
        cfg.method = method;
        cfg.region_ist = region_ist;
        cfg.useSSL = useSSL;
        cfg.useCacheIfPossible = useCacheIfPossible;
        return cfg;
    }

    public TS_SQLConnConfig cloneIt() {
        return cloneItAs(dbName);
    }
}
