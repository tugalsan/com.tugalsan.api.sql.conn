package com.tugalsan.api.sql.conn.server;

import com.tugalsan.api.sql.conn.server.core.TS_SQLConnCoreURLUtils;
import com.tugalsan.api.file.json.server.TS_FileJsonUtils;
import com.tugalsan.api.file.server.TS_DirectoryUtils;
import com.tugalsan.api.file.server.TS_FileUtils;
import com.tugalsan.api.file.txt.server.TS_FileTxtUtils;
import com.tugalsan.api.function.client.maythrowexceptions.checked.TGS_FuncMTCUtils;
import com.tugalsan.api.function.client.maythrowexceptions.unchecked.TGS_FuncMTU_In1;
import com.tugalsan.api.log.server.TS_Log;
import com.tugalsan.api.sql.conn.server.core.TS_SQLConnCoreNewConnection;
import com.tugalsan.api.union.client.TGS_UnionExcuse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.*;

public class TS_SQLConnAnchor {

    final private static TS_Log d = TS_Log.of(TS_SQLConnAnchor.class);

    private TS_SQLConnAnchor(TS_SQLConnConfig config) {
        this.config = config;
    }
    final public TS_SQLConnConfig config;
    public volatile boolean disableUseCacheForAWhile = false;

    public void use(TGS_FuncMTU_In1<Connection> con) {
        try (var conPack = TS_SQLConnCoreNewConnection.of(TS_SQLConnAnchor.this).value()) {
            con.run(conPack.con());
        }
    }

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
        if (url == null) {
            synchronized (this) {
                if (url == null) {
                    url = TS_SQLConnCoreURLUtils.create(config);
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
                    var newProp = new Properties();
                    if (config.charsetUTF8) {
                        newProp.put("charSet", StandardCharsets.UTF_8.name());
                    }
                    if (config.dbUser == null || config.dbUser.equals("") || config.dbPassword == null) {
                    } else {
                        newProp.put("user", config.dbUser);
                        newProp.put("password", config.dbPassword);
                    }
                    prop = newProp;
                }
            }
        }
        return prop;
    }
    private volatile Properties prop;

    @Override
    public String toString() {
        return TS_SQLConnAnchor.class.getSimpleName() + "{" + "config=" + config + ", url=" + url + ", prop=" + prop + '}';
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
