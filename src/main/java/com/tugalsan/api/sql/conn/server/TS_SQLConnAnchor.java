package com.tugalsan.api.sql.conn.server;

import java.util.*;
import org.apache.tomcat.jdbc.pool.*;

public class TS_SQLConnAnchor {

//    final private static TS_Log d = TS_Log.of(TS_SQLConnAnchor.class);
    private TS_SQLConnAnchor(TS_SQLConnConfig config) {
        this.config = config;
    }
    public TS_SQLConnConfig config;

    public static TS_SQLConnAnchor of(TS_SQLConnConfig config) {
        return new TS_SQLConnAnchor(config);
    }

    public TS_SQLConnAnchor cloneItAs(CharSequence newDbName) {
        return new TS_SQLConnAnchor(config.cloneItAs(newDbName));
    }

    @Override
    public int hashCode() {
        var hash = 7;
        hash = 97 * hash + Objects.hashCode(this.config);
        hash = 97 * hash + Objects.hashCode(this.url);
        hash = 97 * hash + Objects.hashCode(this.prop);
        hash = 97 * hash + Objects.hashCode(this.pool);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TS_SQLConnAnchor)) {
            return false;
        }
        var o = (TS_SQLConnAnchor) obj;
        return o.config.equals(this.config);
    }

    public String url() {
        return url == null ? url = TS_SQLConnURLUtils.create(config) : url;
    }
    private String url = null;

    public Properties properties() {
        return prop == null ? TS_SQLConnPropsUtils.create(config) : prop;
    }
    private Properties prop;

    public PoolProperties pool() {
        return pool == null ? pool = TS_SQLConnPoolUtils.create(config) : pool;
    }
    private PoolProperties pool = null;
}
