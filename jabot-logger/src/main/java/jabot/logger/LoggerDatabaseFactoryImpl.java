package jabot.logger;

import jabot.db.DatabaseAbstract;
import jabot.db.DatabaseFactoryAbstract;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class LoggerDatabaseFactoryImpl extends DatabaseFactoryAbstract {

    public LoggerDatabaseFactoryImpl(String connection, String user, String pwd) {
        super(connection, user, pwd);
    }

    @Override
    protected DatabaseAbstract internalCreate() {
        return new LoggerDatabaseImpl();
    }

}
