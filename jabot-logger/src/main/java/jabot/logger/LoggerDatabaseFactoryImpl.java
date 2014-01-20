package jabot.logger;

import jabot.db.Database;
import jabot.db.DatabaseFactory;

import java.sql.SQLException;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class LoggerDatabaseFactoryImpl implements DatabaseFactory {

    private final String connection;
    private final String user;
    private final String pwd;

    public LoggerDatabaseFactoryImpl(String connection, String user, String pwd) {
        this.connection = connection;
        this.user = user;
        this.pwd = pwd;
    }

    @Override
    public Database create() throws SQLException {
        LoggerDatabaseImpl db = new LoggerDatabaseImpl();
        db.init(connection, user, pwd);
        db.check();
        return db;
    }
}
