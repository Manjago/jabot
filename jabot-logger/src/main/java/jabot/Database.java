package jabot;

import org.h2.jdbcx.JdbcConnectionPool;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class Database implements AutoCloseable{

    private final JdbcConnectionPool cp;

    private Database(JdbcConnectionPool cp) {
        this.cp = cp;
    }

    public static Database init(String connection, String user, String pwd){
        JdbcConnectionPool cp = JdbcConnectionPool.create(
                connection, user, pwd);
        return new Database(cp);
    }

    public void executeSql(String sql) throws SQLException {

        if (cp == null || sql == null){
            return;
        }

        try(Connection conn = cp.getConnection()){
            conn.createStatement().execute(sql);
        }
    }

    @Override
    public void close() throws Exception {
        if (cp != null){
            cp.dispose();
        }
    }
}
