package com.tugalsan.api.sql.conn.server;

import com.tugalsan.api.file.json.server.*;
import com.tugalsan.api.file.server.*;
import com.tugalsan.api.file.txt.server.*;
import com.tugalsan.api.log.server.*;
import com.tugalsan.api.union.client.TGS_UnionExcuse;
import java.nio.file.*;

public class TS_SQLConnAnchorUtils {

    final private static TS_Log d = TS_Log.of(TS_SQLConnAnchorUtils.class);

    public static TGS_UnionExcuse<TS_SQLConnAnchor> createAnchor(Path dir, CharSequence dbName) {
        var u_dir_create = TS_DirectoryUtils.createDirectoriesIfNotExists(dir);
        if (u_dir_create.isExcuse()) {
            return u_dir_create.toExcuse();
        }
        var filePath = dir.resolve(TS_SQLConnConfig.class.getSimpleName() + "_" + dbName + ".json");
        d.cr("createAnchor", filePath);
        if (!TS_FileUtils.isExistFile(filePath)) {//IF NOT EXISTS -> WRITE DEFAFAULT
            var tmp = TS_SQLConnConfig.of(dbName);
            var u_jsonString = TS_FileJsonUtils.toJSON(tmp, true);
            if (u_jsonString.isExcuse()) {
                return u_jsonString.toExcuse();
            }
            var u_write = TS_FileJsonUtils.toFile(u_jsonString.value(), filePath, false, true);
            if (u_write.isExcuse()) {
                return u_write.toExcuse();
            }
        }
        var u_read = TS_FileTxtUtils.toString(filePath);
        if (u_read.isExcuse()) {
            return u_read.toExcuse();
        }
        var u_config = TS_FileJsonUtils.toObject(u_read.value(), TS_SQLConnConfig.class);
        if (u_config.isExcuse()) {
            return u_config.toExcuse();
        }
        return TGS_UnionExcuse.of(TS_SQLConnAnchor.of(u_config.value()));
    }
}
