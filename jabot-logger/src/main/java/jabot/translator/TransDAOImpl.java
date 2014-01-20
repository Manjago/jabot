package jabot.translator;

import jabot.db.Database;
import jabot.translator.dto.TransUser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class TransDAOImpl implements TransDAO {

    private Database db;

    private static final String INSERT = "INSERT INTO TRANSUSERS ( JID , ENABLED )\n" +
            "VALUES (?, ?)";
    private static final String SELECT_BY_JID = "SELECT ID, JID, ENABLED FROM TRANSUSERS WHERE JID = ?";

    public TransDAOImpl(Database db) {
        this.db = db;
    }

    @Override
    public void store(TransUser user) throws SQLException {
        try (Connection conn = db.getConnection()) {

            try (PreparedStatement ps = conn.prepareStatement(INSERT)) {
                storeTransUser(ps, user);
            }
            conn.commit();
        }

    }

    private void storeTransUser(PreparedStatement ps, TransUser user) throws SQLException {
        final int jidIndex = 1;
        final int enabledIndex = 2;

        ps.setString(jidIndex, user.getJid());
        ps.setBoolean(enabledIndex, user.isEnabled());
        ps.execute();

    }

    @Override
    public TransUser get(String jid) throws SQLException {
        try (Connection conn = db.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_JID)) {
                ps.setString(1, jid);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.first()) {
                        return null;
                    }

                    return loadTransUser(rs);
                }
            }
        }
    }

    private TransUser loadTransUser(ResultSet rs) throws SQLException {
        TransUser r = new TransUser();
        r.setId(rs.getLong("ID"));
        r.setJid(rs.getString("JID"));
        r.setEnabled(rs.getBoolean("ENABLED"));
        return r;
    }

    @Override
    public void update(TransUser user) throws SQLException {

    }

    @Override
    public void delete(String jid) throws SQLException {

    }
}
