package com.tugalsan.api.sql.conn.server;

import javax.sql.DataSource;
import com.tugalsan.api.union.client.TGS_UnionExcuse;
import java.util.Objects;

public record TS_SQLConnSource(TS_SQLConnAnchor anchor, org.apache.tomcat.jdbc.pool.DataSource main, TGS_UnionExcuse<DataSource> proxy) {

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + Objects.hashCode(this.anchor);
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
        final TS_SQLConnSource other = (TS_SQLConnSource) obj;
        return Objects.equals(this.anchor, other.anchor);
    }

}
