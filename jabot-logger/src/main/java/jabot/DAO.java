package jabot;

import jabot.dto.LogEntry;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public interface DAO {
    long store(LogEntry logEntry) throws SQLException;
    LogEntry getById(long id) throws SQLException;
    List<LogEntry> getByPeriod(Date from, Date to) throws SQLException;
    List<LogEntry> getByReg(String reg, int limit) throws SQLException;
}
