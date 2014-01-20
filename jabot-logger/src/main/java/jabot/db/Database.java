package jabot.db;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public interface Database extends AutoCloseable {
    Connection getConnection() throws SQLException;
}
