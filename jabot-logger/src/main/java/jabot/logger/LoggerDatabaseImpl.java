package jabot.logger;

import jabot.db.DatabaseAbstract;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class LoggerDatabaseImpl extends DatabaseAbstract {

    protected void check() throws SQLException {
        try (Connection conn = getConnection()) {

            if (isNeedDbCreate(conn, "LOGDATA")) {
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
                execStatement(conn, "ALTER TABLE LOGDATA ADD CONSTRAINT unique_ID UNIQUE (ID);");
                execStatement(conn, "CREATE INDEX EVENTDATE_index ON LOGDATA ( EVENTDATE );");
                execStatement(conn, "CREATE ALIAS FINDBYREGEXP FOR \"jabot.logger.UserFunctions.findByPattern\";");
            }

        }

    }

}
