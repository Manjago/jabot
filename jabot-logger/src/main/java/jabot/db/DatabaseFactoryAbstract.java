package jabot.db;

import java.sql.SQLException;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public abstract class DatabaseFactoryAbstract implements DatabaseFactory {

    private final String connection;
    private final String user;
    private final String pwd;

    public DatabaseFactoryAbstract(String connection, String user, String pwd) {
        this.connection = connection;
        this.user = user;
        this.pwd = pwd;
    }

    protected String getConnection() {
        return connection;
    }

    protected String getUser() {
        return user;
    }

    protected String getPwd() {
        return pwd;
    }

    protected abstract DatabaseAbstract internalCreate();

    @Override
    public Database create() throws SQLException {
        DatabaseAbstract db = internalCreate();
        db.init(getConnection(), getUser(), getPwd());
        db.check();
        return db;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DatabaseFactoryAbstract{");
        sb.append("connection='").append(connection).append('\'');
        sb.append(", user='").append(user).append('\'');
        sb.append(", pwd='").append(pwd).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
