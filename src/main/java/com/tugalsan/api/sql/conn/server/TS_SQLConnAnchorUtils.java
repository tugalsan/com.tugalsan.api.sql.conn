package com.tugalsan.api.sql.conn.server;

import com.tugalsan.api.file.json.server.*;
import com.tugalsan.api.file.server.*;
import com.tugalsan.api.file.txt.server.*;
import com.tugalsan.api.log.server.*;
import com.tugalsan.api.unsafe.client.*;
import java.nio.file.*;

public class TS_SQLConnAnchorUtils {

    final private static TS_Log d = TS_Log.of(TS_SQLConnAnchorUtils.class);

    public static TS_SQLConnAnchor createAnchor(Path dir, CharSequence dbName) {
        TS_DirectoryUtils.assureExists(dir);
        var filePath = dir.resolve(TS_SQLConnConfig.class.getSimpleName() + "_" + dbName + ".json");
        d.cr("getInstance", filePath);

        if (!TS_FileUtils.isExistFile(filePath)) {
            TS_DirectoryUtils.createDirectoriesIfNotExists(filePath.getParent());
            var tmp = new TS_SQLConnConfig(dbName);
            var jsonString = TS_FileJsonUtils.toJSON(tmp, true);
            TS_FileJsonUtils.toFile(jsonString, filePath, false, true);
        }

        var jsonString = TGS_UnSafe.compile(() -> TS_FileTxtUtils.toString(filePath), e -> {
            d.ct("getDefaultInstance", e);
            d.cr("getDefaultInstance", "writing default file");
            var tmp = new TS_SQLConnConfig(dbName);
            var jsonString0 = TS_FileJsonUtils.toJSON(tmp, true);
            TS_FileTxtUtils.toFile(jsonString0, filePath, false);
            return jsonString0;
        });
        d.ci("getDefaultInstance", jsonString);

        var config = TS_FileJsonUtils.toObject(jsonString, TS_SQLConnConfig.class);
        return new TS_SQLConnAnchor(config);
    }
}
