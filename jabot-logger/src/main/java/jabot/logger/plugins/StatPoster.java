package jabot.logger.plugins;

import jabot.Helper;
import jabot.logger.DAO;
import jabot.logger.DAOImpl;
import jabot.logger.Database;
import jabot.logger.dto.LogEntry;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class StatPoster {
    private final Database initedDb;
    private final DisplayLogEntry displayLogEntry;

    public StatPoster(Database initedDb) {
        this.initedDb = initedDb;
        displayLogEntry = new DisplayLogEntry();
    }

    public String report(Date from, Date to) throws SQLException {

        DAO dao = new DAOImpl(initedDb);

        List<LogEntry> logEntries = dao.getByPeriod(from, to);

        StringBuilder sb = new StringBuilder();
        for (LogEntry entry : logEntries) {
            final String str = display(entry);
            if (Helper.isEmptyStr(str)) {
                continue;
            }
            sb.append(str);
            sb.append('\n');
        }

        return sb.toString();
    }

    private String display(LogEntry logEntry) {
        return displayLogEntry.display(logEntry);
    }
}
