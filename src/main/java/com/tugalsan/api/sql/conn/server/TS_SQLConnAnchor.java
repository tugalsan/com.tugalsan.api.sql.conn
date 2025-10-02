package com.tugalsan.api.sql.conn.server;

import module com.tugalsan.api.file.json;
import module com.tugalsan.api.file;
import module com.tugalsan.api.file.txt;
import module com.tugalsan.api.function;
import module com.tugalsan.api.log;
import com.tugalsan.api.os.server.TS_OsCpuUtils;
import module com.tugalsan.api.union;
import module java.sql;
import com.tugalsan.api.sql.conn.server.core.*;
import com.tugalsan.api.thread.server.sync.rateLimited.TS_ThreadSyncRateLimitedRun;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.function.Supplier;

public class TS_SQLConnAnchor {

    final private static TS_Log d = TS_Log.of(TS_SQLConnAnchor.class);

    private TS_SQLConnAnchor(TS_SQLConnConfig config) {
        this.config = config;
    }
    final public TS_SQLConnConfig config;
    public volatile boolean disableUseCacheForAWhile = false;//class.TS_LibRqlBufferCreateUtils and func.tagSelectAndSpace uses it

    public void use(TGS_FuncMTU_In1<Connection> con) {
        TS_ThreadSyncRateLimitedRun.of(use_sema.get()).run(() -> {
            try (var conPack = TS_SQLConnCoreNewConnection.of(TS_SQLConnAnchor.this).value()) {
                con.run(conPack.con());
            }
        });
    }
    final private static Supplier<Semaphore> use_sema = StableValue.supplier(() -> new Semaphore(TS_OsCpuUtils.getProcessorCount() - 1));

    public static TS_SQLConnAnchor of(TS_SQLConnConfig config) {
        return new TS_SQLConnAnchor(config);
    }

    public static TGS_UnionExcuse<TS_SQLConnAnchor> of(Path dir, CharSequence dbName) {
        TS_DirectoryUtils.assureExists(dir);
        var filePath = dir.resolve(TS_SQLConnConfig.class.getSimpleName() + "_" + dbName + ".json");
        d.cr("createAnchor", filePath);

        if (!TS_FileUtils.isExistFile(filePath)) {
            TS_DirectoryUtils.createDirectoriesIfNotExists(filePath.getParent());
            var tmp = TS_SQLConnConfig.of(dbName);
            var jsonString = TS_FileJsonUtils.toJSON(tmp, true);
            TS_FileJsonUtils.toFile(jsonString, filePath, false, true);
        }

        var jsonString = TGS_FuncMTCUtils.call(() -> TS_FileTxtUtils.toString(filePath), e -> {
            d.ct("createAnchor", e);
            d.cr("createAnchor", "writing default file");
            var tmp = TS_SQLConnConfig.of(dbName);
            var jsonString0 = TS_FileJsonUtils.toJSON(tmp, true);
            TS_FileTxtUtils.toFile(jsonString0, filePath, false);
            return jsonString0;
        });
        d.ci("createAnchor", jsonString);

        var u_config = TS_FileJsonUtils.toObject(jsonString, TS_SQLConnConfig.class);
        if (u_config.isExcuse()) {
            return TGS_UnionExcuse.ofExcuse(u_config.excuse());
        }
        return TGS_UnionExcuse.of(TS_SQLConnAnchor.of(u_config.value()));
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
        return url.orElseSet(() -> TS_SQLConnCoreURLUtils.create(config));
    }
    private volatile StableValue<String> url = StableValue.of();

    public Properties properties() {
        return prop.orElseSet(() -> {
            var newProp = new Properties();
            if (config.charsetUTF8) {
                newProp.put("charSet", StandardCharsets.UTF_8.name());
            }
            if (config.dbUser == null || config.dbUser.equals("") || config.dbPassword == null) {
            } else {
                newProp.put("user", config.dbUser);
                newProp.put("password", config.dbPassword);
            }
            return newProp;
        });
    }
    private volatile StableValue<Properties> prop = StableValue.of();

    @Override
    public String toString() {
        return TS_SQLConnAnchor.class.getSimpleName() + "{" + "config=" + config + ", url=" + url.orElse("null") + ", prop=" + prop.orElse(new Properties()) + '}';
    }

    public String tagSelectAndSpace() {
        if (config.method == TS_SQLConnMethodUtils.METHOD_MARIADB()) {
            if (!disableUseCacheForAWhile) {
                if (config.useCacheIfPossible) {
                    return "SELECT SQL_CACHE ";
                }
            }
        }
        return "SELECT ";
    }
}
