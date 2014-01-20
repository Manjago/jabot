package jabot.logger.plugins;

import jabot.Helper;
import jabot.db.Database;
import jabot.logger.LoggerDAO;
import jabot.logger.LoggerDAOImpl;
import jabot.logger.dto.LogEntry;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class StatPoster {
    private final Database initedDb;
    private final LogEntryDisplayer logEntryDisplayer;

    public StatPoster(Database initedDb) {
        this.initedDb = initedDb;
        logEntryDisplayer = new LogEntryDisplayer();
    }

    public String report(Date from, Date to) throws SQLException {

        LoggerDAO loggerDao = new LoggerDAOImpl(initedDb);

        List<LogEntry> logEntries = loggerDao.getByPeriod(from, to);

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
        return logEntryDisplayer.display(logEntry);
    }
}
