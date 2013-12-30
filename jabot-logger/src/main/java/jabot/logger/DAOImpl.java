package jabot.logger;

import jabot.logger.dto.EntryType;
import jabot.logger.dto.LogEntry;

import java.io.StringReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class DAOImpl implements DAO {

    private static final String INSERT_LOG_ENTRY = "INSERT INTO LOGDATA ( CONFERENCE , ENTRYTYPE , EVENTDATE , NICK , TEXT, FROMME, MSGTYPE )\n" +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String SELECT_BY_ID = "SELECT ID, EVENTDATE, TEXT, CONFERENCE, NICK, FROMME, MSGTYPE FROM LOGDATA WHERE ID = ?";
    private static final String SELECT_BY_PERIOD = "SELECT ID, EVENTDATE, TEXT, CONFERENCE, NICK, FROMME, MSGTYPE FROM LOGDATA WHERE EVENTDATE BETWEEN ? AND ? ORDER BY EVENTDATE";
    private static final String SELECT_BY_REGEXP = "SELECT ID, EVENTDATE, TEXT, CONFERENCE, NICK, FROMME, MSGTYPE FROM LOGDATA WHERE FINDBYREGEXP(TEXT, ?) <> 0 AND MSGTYPE = 0 ORDER BY EVENTDATE DESC LIMIT ?";
    private Database db;

    private static void storeLogEntry(PreparedStatement ps, LogEntry logEntry) throws SQLException {
        final int conferenceIndex = 1;
        final int entryTypeIndex = 2;
        final int eventDateIndex = 3;
        final int nickIndex = 4;
        final int textIndex = 5;
        final int fromeIndex = 6;
        final int msgtypeIndex = 7;

        ps.setString(conferenceIndex, logEntry.getConference());
        ps.setByte(entryTypeIndex, (byte) 0);
        ps.setTimestamp(eventDateIndex, new Timestamp(logEntry.getEventDate().getTime()));
        ps.setString(nickIndex, logEntry.getFrom());
        ps.setClob(textIndex, new StringReader(logEntry.getText()));
        ps.setBoolean(fromeIndex, logEntry.isFromMe());
        ps.setByte(msgtypeIndex, logEntry.getEntryType().getMsgType());
        ps.execute();
    }

    private static LogEntry loadLogEntry(ResultSet rs) throws SQLException {
        LogEntry r = new LogEntry();
        r.setConference(rs.getString("CONFERENCE"));
        r.setEventDate(rs.getTimestamp("EVENTDATE"));
        r.setFrom(rs.getString("NICK"));
        r.setId(rs.getLong("ID"));
        r.setFromMe(rs.getBoolean("FROMME"));
        r.setEntryType(EntryType.fromMsgType(rs.getByte("MSGTYPE")));

        Clob clob = rs.getClob("TEXT");
        if (clob != null) {
            r.setText(clob.getSubString(1, (int) clob.length()));
        }
        return r;
    }

    public void setDb(Database db) {
        this.db = db;
    }

    @Override
    public void store(LogEntry logEntry) throws SQLException {

        if (logEntry == null || !logEntry.isValid()) {
            throw new IllegalArgumentException("logEntry: " + logEntry);
        }

        try (Connection conn = db.getConnection()) {

            try (PreparedStatement ps = conn.prepareStatement(INSERT_LOG_ENTRY)) {
                storeLogEntry(ps, logEntry);
            }

            conn.commit();

        }


    }

    @Override
    public void store(List<LogEntry> logEntries) throws SQLException {
        if (logEntries == null) {
            throw new IllegalArgumentException();
        }

        for (LogEntry l : logEntries) {
            if (!l.isValid()) {
                throw new IllegalArgumentException(l.toString());
            }
        }

        try (Connection conn = db.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(INSERT_LOG_ENTRY)) {
                for (LogEntry logEntry : logEntries) {
                    storeLogEntry(ps, logEntry);
                }

                conn.commit();
            }

        }

    }

    @Override
    public LogEntry getById(long id) throws SQLException {
        try (Connection conn = db.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID)) {
                ps.setLong(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.first()) {
                        return null;
                    }

                    return loadLogEntry(rs);
                }
            }
        }
    }

    @Override
    public List<LogEntry> getByPeriod(Date from, Date to) throws SQLException {

        if (from == null || to == null) {
            throw new IllegalArgumentException("bad args " + from + " " + to);
        }

        try (Connection conn = db.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_PERIOD)) {
                ps.setTimestamp(1, new Timestamp(from.getTime()));
                ps.setTimestamp(2, new Timestamp(to.getTime()));
                try (ResultSet rs = ps.executeQuery()) {
                    List<LogEntry> result = new ArrayList<>();
                    extractLogEntryList(rs, result);
                    return result;
                }


            }
        }
    }

    @Override
    public List<LogEntry> getByReg(String reg, int limit) throws SQLException {
        if (reg == null || limit <= 0) {
            throw new IllegalArgumentException();
        }

        try (Connection conn = db.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_REGEXP)) {
                final int regExpIndex = 1;
                final int limitIndex = 2;

                ps.setString(regExpIndex, reg);
                ps.setInt(limitIndex, limit);

                try (ResultSet rs = ps.executeQuery()) {
                    List<LogEntry> result = new ArrayList<>();
                    extractLogEntryList(rs, result);
                    return result;
                }
            }
        }


    }

    private void extractLogEntryList(ResultSet rs, List<LogEntry> result) throws SQLException {
        while (rs.next()) {
            LogEntry r = loadLogEntry(rs);
            result.add(r);
        }
    }
}
