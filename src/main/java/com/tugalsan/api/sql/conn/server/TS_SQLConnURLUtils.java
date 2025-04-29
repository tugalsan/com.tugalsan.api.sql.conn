package com.tugalsan.api.sql.conn.server;

import com.tugalsan.api.log.server.TS_Log;
import java.nio.charset.StandardCharsets;

public class TS_SQLConnURLUtils {

    private TS_SQLConnURLUtils() {

    }

    public static boolean USE_NEW_SSLMODE = true;

    final private static TS_Log d = TS_Log.of(TS_SQLConnURLUtils.class);

    private static String PARAMETER_AND() {
        return "&";
    }//&amp;";

    private static String PARAMETER_RECONNECT_TRUE() {
        return "autoReconnect=true" + PARAMETER_AND() + "autoReconnectForPools=true";
    }

    private static String PARAMETER_RECONNECT_FALSE() {
        return "autoReconnect=false" + PARAMETER_AND() + "autoReconnectForPools=false";
    }

    private static String PARAMETER_SSL_TRUE() {
        return USE_NEW_SSLMODE ? "sslMode=TRUST" : "sslMode=REQUIRED";
    }

    private static String PARAMETER_SSL_FALSE() {
        return "sslMode=DISABLED";
    }

    private static String PARAMETER_REGION_IST() {
        return "useLegacyDatetimeCode=false" + PARAMETER_AND() + "serverTimezone=Europe/Istanbul";
    }

    private static String PARAMETER_CHARSET() {
        return "useUnicode=true" + PARAMETER_AND() + "characterEncoding=" + StandardCharsets.UTF_8.name();
    }

    public static String create(TS_SQLConnConfig config) {
        var sb = new StringBuilder();
        sb.append("jdbc:").append(TS_SQLConnMethodUtils.getDriverProtocol(config)).append("://").append(config.dbIp).append(":").append(config.dbPort).append("/").append(config.dbName);
        sb.append("?");
        if (config.charsetUTF8) {
            sb.append(PARAMETER_CHARSET());
        }
        sb.append(PARAMETER_AND()).append(config.useSSL ? PARAMETER_SSL_TRUE() : PARAMETER_SSL_FALSE());
        sb.append(PARAMETER_AND()).append(config.autoReconnect ? PARAMETER_RECONNECT_TRUE() : PARAMETER_RECONNECT_FALSE());
        if (config.region_ist) {
            sb.append(PARAMETER_AND()).append(PARAMETER_REGION_IST());
        }
        var url = sb.toString();
        d.ci("create", url);
        return url;
    }
}
