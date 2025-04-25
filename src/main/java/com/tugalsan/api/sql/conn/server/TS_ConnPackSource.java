package com.tugalsan.api.sql.conn.server;

import javax.sql.DataSource;
import com.tugalsan.api.union.client.TGS_UnionExcuse;

public record TS_ConnPackSource(TS_SQLConnAnchor anchor, DataSource main, TGS_UnionExcuse<DataSource> proxy) {

}
