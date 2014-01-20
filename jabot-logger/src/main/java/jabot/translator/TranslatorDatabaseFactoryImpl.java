package jabot.translator;

import jabot.db.DatabaseAbstract;
import jabot.db.DatabaseFactoryAbstract;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class TranslatorDatabaseFactoryImpl extends DatabaseFactoryAbstract {

    public TranslatorDatabaseFactoryImpl(String connection, String user, String pwd) {
        super(connection, user, pwd);
    }

    @Override
    protected DatabaseAbstract internalCreate() {
        return new TranslatorDatabaseImpl();
    }

}
