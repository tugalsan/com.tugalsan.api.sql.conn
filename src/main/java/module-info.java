module com.tugalsan.api.sql.conn {
    requires java.sql;
    requires tomcat.jdbc;
    requires com.tugalsan.api.unsafe;
    requires com.tugalsan.api.validator;
    requires com.tugalsan.api.profile;
    requires com.tugalsan.api.compiler;
    requires com.tugalsan.api.executable;
    requires com.tugalsan.api.string;
    requires com.tugalsan.api.log;
    requires com.tugalsan.api.list;
    requires com.tugalsan.api.file;
    requires com.tugalsan.api.file.obj;
    requires com.tugalsan.api.file.json;
    requires com.tugalsan.api.file.txt;
    requires com.tugalsan.api.pack;
    requires com.tugalsan.api.sql.col.typed;
    requires com.tugalsan.api.sql.sanitize;
    requires com.tugalsan.api.sql.resultset;
    exports com.tugalsan.api.sql.conn.server;
}
