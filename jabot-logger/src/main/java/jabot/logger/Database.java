package jabot.logger;

import org.h2.jdbcx.JdbcConnectionPool;

import java.sql.*;

import static jabot.Helper.checkNotNull;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public final class Database implements AutoCloseable {

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
        Connection connection = cp.getConnection();
        connection.setAutoCommit(false);
        return connection;
    }

    public void check() throws SQLException {
        try (Connection conn = getConnection()) {

            if (isNeedDbCreate(conn)) {
                execStatement(conn, "CREATE TABLE LOGDATA\n" +
                        "(\n" +
                        "    ID IDENTITY PRIMARY KEY NOT NULL,\n" +
                        "    EVENTDATE TIMESTAMP NOT NULL,\n" +
                        "    TEXT CLOB NOT NULL,\n" +
                        "    CONFERENCE VARCHAR(200) NOT NULL,\n" +
                        "    NICK VARCHAR(200) NOT NULL,\n" +
                        "    ENTRYTYPE TINYINT NOT NULL, \n" +
                        "    FROMME BOOLEAN NOT NULL,\n" +
                        "    MSGTYPE TINYINT NOT NULL,\n" +
                        "    DELAY TIMESTAMP\n" +
                        ");\n");
                execStatement(conn, "ALTER TABLE PUBLIC.LOGDATA ADD CONSTRAINT unique_ID UNIQUE (ID);");
                execStatement(conn, "CREATE INDEX EVENTDATE_index ON PUBLIC.LOGDATA ( EVENTDATE );");
                execStatement(conn, "CREATE ALIAS FINDBYREGEXP FOR \"jabot.logger.UserFunctions.findByPattern\";");
            }

        }
    }

    private static boolean isNeedDbCreate(Connection conn) throws SQLException {
        try(PreparedStatement checkTable = conn.prepareStatement("SELECT 1 FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = ?")){
            checkTable.setString(1, "LOGDATA");
            try(ResultSet rs = checkTable.executeQuery()){
                return !rs.first();
            }
        }
    }

    private static void execStatement(Connection conn, String sql) throws SQLException {
        try(Statement statement = checkNotNull(conn).createStatement()){
            statement.execute(checkNotNull(sql));
        }
    }

    @Override
    public void close() {
        if (cp != null) {
            cp.dispose();
        }
    }
}
