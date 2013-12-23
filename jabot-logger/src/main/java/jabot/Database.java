package jabot;

import org.h2.jdbcx.JdbcConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class Database implements AutoCloseable {

    private final JdbcConnectionPool cp;

    private Database(JdbcConnectionPool cp) {
        this.cp = cp;
    }

    public static Database init(String connection, String user, String pwd) {
        JdbcConnectionPool cp = JdbcConnectionPool.create(
                connection, user, pwd);
        return new Database(cp);
    }

    public Connection getConnection() throws SQLException {
        final Connection connection = cp.getConnection();
        connection.setAutoCommit(false);
        return connection;
    }

    public void check() throws SQLException {
        try (Connection conn = getConnection()) {
            PreparedStatement checkTable = conn.prepareStatement("SELECT 1 FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = ?");
            checkTable.setString(1, "LOGDATA");
            ResultSet rs = checkTable.executeQuery();
            boolean needDbCreate = !rs.first();
            if (needDbCreate) {
                conn.createStatement().execute("CREATE TABLE LOGDATA\n" +
                        "(\n" +
                        "    ID IDENTITY PRIMARY KEY NOT NULL,\n" +
                        "    EVENTDATE TIMESTAMP NOT NULL,\n" +
                        "    TEXT CLOB NOT NULL,\n" +
                        "    CONFERENCE VARCHAR(200) NOT NULL,\n" +
                        "    NICK VARCHAR(200) NOT NULL,\n" +
                        "    ENTRYTYPE TINYINT NOT NULL\n" +
                        ");\n");
                conn.createStatement().execute("ALTER TABLE PUBLIC.LOGDATA ADD CONSTRAINT unique_ID UNIQUE (ID);");
                conn.createStatement().execute("CREATE INDEX EVENTDATE_index ON PUBLIC.LOGDATA ( EVENTDATE );");
            }

        }
    }

    public void executeSql(String sql) throws SQLException {

        if (cp == null || sql == null) {
            return;
        }

        try (Connection conn = cp.getConnection()) {
            conn.createStatement().execute(sql);
        }
    }

    @Override
    public void close() throws Exception {
        if (cp != null) {
            cp.dispose();
        }
    }
}
