package jabot;

import jabot.dto.LogEntry;

import java.io.StringReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class DAOImpl implements DAO {

    private Database db;

    public void setDb(Database db) {
        this.db = db;
    }

    @Override
    public long store(LogEntry logEntry) throws SQLException {

        if (logEntry == null || !logEntry.isValid()) {
            throw new IllegalArgumentException("logEntry: " + String.valueOf(logEntry));
        }

        try (Connection conn = db.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO LOGDATA ( CONFERENCE , ENTRYTYPE , EVENTDATE , NICK , TEXT )\n" +
                    "VALUES (?, ?, ?, ?, ?)");

            ps.setString(1, logEntry.getConference());
            ps.setByte(2, (byte) 0);
            ps.setTimestamp(3, new Timestamp(logEntry.getEventDate().getTime()));
            ps.setString(4, logEntry.getFrom());
            ps.setClob(5, new StringReader(logEntry.getText()));
            ps.execute();


            CallableStatement cs = conn.prepareCall("{ ? = call IDENTITY()}");
            cs.registerOutParameter(1, Types.BIGINT);
            cs.execute();
            long id = cs.getLong(1);

            conn.commit();

            return id;
        }


    }

    @Override
    public LogEntry getById(long id) throws SQLException {
        try (Connection conn = db.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT ID, EVENTDATE, TEXT , CONFERENCE , NICK  FROM LOGDATA WHERE ID = ?");

            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();

            if (!rs.first()){
                return null;
            }

            LogEntry r = new LogEntry();
            r.setConference(rs.getString("CONFERENCE"));
            r.setEventDate(rs.getTimestamp("EVENTDATE"));
            r.setFrom(rs.getString("NICK"));
            r.setId(rs.getLong("ID"));

            Clob clob = rs.getClob("TEXT");
            if (clob != null){
                r.setText(clob.getSubString(1, (int) clob.length()));
            }

            return r;

        }
    }

    @Override
    public List<LogEntry> getByPeriod(Date from, Date to) throws SQLException {

        if (from == null || to == null){
            throw new IllegalArgumentException("bad args " + String.valueOf(from) + " " + String.valueOf(to));
        }

        try (Connection conn = db.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT ID, EVENTDATE, TEXT, CONFERENCE, NICK FROM LOGDATA WHERE EVENTDATE BETWEEN ? AND ? ORDER BY EVENTDATE");

            ps.setTimestamp(1, new Timestamp(from.getTime()));
            ps.setTimestamp(2, new Timestamp(to.getTime()));
            ResultSet rs = ps.executeQuery();

            List<LogEntry> result = new ArrayList<>();

            extractLogEntryList(rs, result);

            return result;

        }
    }

    @Override
    public List<LogEntry> getByReg(String reg, int limit) throws SQLException {
        if (reg == null || limit <=0){
            throw new IllegalArgumentException();
        }

        try (Connection conn = db.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT ID, EVENTDATE, TEXT, CONFERENCE, NICK FROM LOGDATA WHERE FINDBYREGEXP(TEXT, ?) <> 0 ORDER BY EVENTDATE DESC LIMIT ?");

            ps.setString(1, reg);
            ps.setInt(2, limit);
            ResultSet rs = ps.executeQuery();

            List<LogEntry> result = new ArrayList<>();

            extractLogEntryList(rs, result);

            return result;

        }


    }

    private void extractLogEntryList(ResultSet rs, List<LogEntry> result) throws SQLException {
        while(rs.next()){
            LogEntry r = new LogEntry();
            r.setConference(rs.getString("CONFERENCE"));
            r.setEventDate(rs.getTimestamp("EVENTDATE"));
            r.setFrom(rs.getString("NICK"));
            r.setId(rs.getLong("ID"));

            Clob clob = rs.getClob("TEXT");
            if (clob != null){
                r.setText(clob.getSubString(1, (int) clob.length()));
            }

            result.add(r);

        }
    }
}
