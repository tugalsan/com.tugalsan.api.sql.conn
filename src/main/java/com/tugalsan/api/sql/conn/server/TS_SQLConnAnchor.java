package com.tugalsan.api.sql.conn.server;

import com.tugalsan.api.function.client.maythrowexceptions.unchecked.TGS_FuncMTU_In1;
import com.tugalsan.api.thread.server.sync.rateLimited.TS_ThreadSyncRateLimitedRun;
import com.tugalsan.api.union.client.TGS_UnionExcuse;
import java.sql.Connection;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.Semaphore;
import org.apache.tomcat.jdbc.pool.*;

public class TS_SQLConnAnchor {

//    final private static TS_Log d = TS_Log.of(TS_SQLConnAnchor.class);
    private TS_SQLConnAnchor(TS_SQLConnConfig config) {
        this.config = config;
        durWait = Duration.ofMillis(config.pool_max_wait_ms);
        conRatedLimited = TS_ThreadSyncRateLimitedRun.of(new Semaphore(config.pool_concurrent));
    }
    public volatile TS_SQLConnConfig config;
    final private Duration durWait;
    final private TS_ThreadSyncRateLimitedRun conRatedLimited;

    public void conRatedLimited(TS_SQLConnAnchor anchor, TGS_FuncMTU_In1<Connection> con) {
        conRatedLimited.runUntil(() -> {
            try (var conPack = TS_SQLConnConUtils.conPack(anchor).value()) {
                con.run(conPack.con());
            }
        }, durWait);
    }

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
        if (url == null) {
            synchronized (this) {
                if (url == null) {
                    url = TS_SQLConnURLUtils.create(config);
                }
            }
        }
        return url;
    }
    private volatile String url = null;

    public Properties properties() {
        if (prop == null) {
            synchronized (this) {
                if (prop == null) {
                    prop = TS_SQLConnPropsUtils.create(config);
                }
            }
        }
        return prop;
    }
    private volatile Properties prop;

    public PoolProperties pool() {
        if (pool == null) {
            synchronized (this) {
                if (pool == null) {
                    pool = TS_SQLConnPoolUtils.create(config);
                }
            }
        }
        return pool;
    }
    private volatile PoolProperties pool = null;
}
