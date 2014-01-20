package jabot.db;

import org.h2.jdbcx.JdbcConnectionPool;

import java.sql.*;

import static jabot.Helper.checkNotNull;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public abstract class DatabaseAbstract implements Database {

    private JdbcConnectionPool cp;

    protected boolean isNeedDbCreate(Connection conn, String tableName) throws SQLException {
        try (PreparedStatement checkTable = conn.prepareStatement("SELECT 1 FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = ?")) {
            checkTable.setString(1, tableName);
            try (ResultSet rs = checkTable.executeQuery()) {
                return !rs.first();
            }
        }
    }

    protected static void execStatement(Connection conn, String sql) throws SQLException {
        try (Statement statement = checkNotNull(conn).createStatement()) {
            statement.execute(checkNotNull(sql));
        }
    }

    public void init(String connection, String user, String pwd) {
        cp = JdbcConnectionPool.create(
                connection, user, pwd);
    }

    protected abstract void check() throws SQLException;

    @Override
    public Connection getConnection() throws SQLException {
        if (cp == null) {
            throw new IllegalStateException();
        }
        Connection connection = cp.getConnection();
        connection.setAutoCommit(false);
        return connection;
    }

    @Override
    public void close() {
        if (cp != null) {
            cp.dispose();
        }
    }
}
