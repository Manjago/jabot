package jabot.translator;

import jabot.db.DatabaseAbstract;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class TranslatorDatabaseImpl extends DatabaseAbstract {
    @Override
    protected void check() throws SQLException {
        try (Connection conn = getConnection()) {

            if (isNeedDbCreate(conn, "TRANSUSERS")) {
                execStatement(conn, "CREATE TABLE TRANSUSERS\n" +
                        "(\n" +
                        "    ID IDENTITY PRIMARY KEY NOT NULL,\n" +
                        "    JID VARCHAR(200) NOT NULL,\n" +
                        "    ENABLED BOOLEAN DEFAULT FALSE\n" +
                        ");");
                execStatement(conn, "CREATE UNIQUE INDEX UNIQUE_JID_INDEX_B ON TRANSUSERS ( JID );");
            }

        }

    }
}
