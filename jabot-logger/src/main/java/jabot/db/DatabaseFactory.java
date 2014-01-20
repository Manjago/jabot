package jabot.db;

import java.sql.SQLException;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public interface DatabaseFactory {
    Database create() throws SQLException;
}
