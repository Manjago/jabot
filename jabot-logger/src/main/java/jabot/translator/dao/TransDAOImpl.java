package jabot.translator.dao;

import jabot.db.Database;
import jabot.translator.dto.TransUser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class TransDAOImpl implements TransDAO {

    private static final String INSERT = "INSERT INTO TRANSUSERS ( JID , ENABLED )\n" +
            "VALUES (?, ?)";
    private static final String SELECT_BY_JID = "SELECT ID, JID, ENABLED FROM TRANSUSERS WHERE JID = ?";
    private static final String SELECT_BY_ID = "SELECT ID, JID, ENABLED FROM TRANSUSERS WHERE ID = ?";
    private static final String UPDATE = "UPDATE TRANSUSERS SET JID =?, ENABLED =? \n" +
            "WHERE ID = ?";
    private static final String DELETE = "DELETE TRANSUSERS WHERE JID = ?";
    private static final String SELECT = "SELECT ID, JID, ENABLED FROM TRANSUSERS";
    private Database db;

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

    @Override
    public TransUser get(long id) throws SQLException {
        try (Connection conn = db.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID)) {
                ps.setLong(1, id);
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
        try (Connection conn = db.getConnection()) {

            try (PreparedStatement ps = conn.prepareStatement(UPDATE)) {
                updateTransUser(ps, user);
            }
            conn.commit();
        }

    }

    private void updateTransUser(PreparedStatement ps, TransUser user) throws SQLException {
        final int jidIndex = 1;
        final int enabledIndex = 2;
        final int idIndex = 3;

        ps.setString(jidIndex, user.getJid());
        ps.setBoolean(enabledIndex, user.isEnabled());
        ps.setLong(idIndex, user.getId());
        ps.execute();
    }

    @Override
    public void delete(String jid) throws SQLException {
        try (Connection conn = db.getConnection()) {

            try (PreparedStatement ps = conn.prepareStatement(DELETE)) {
                deleteTransUser(ps, jid);
            }
            conn.commit();
        }

    }

    @Override
    public List<TransUser> getAll() throws SQLException {
        try (Connection conn = db.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(SELECT)) {
                try (ResultSet rs = ps.executeQuery()) {
                    List<TransUser> result = new ArrayList<>();
                    extractTransUserList(rs, result);
                    return result;
                }
            }
        }
    }

    private void extractTransUserList(ResultSet rs, List<TransUser> result) throws SQLException {
        while (rs.next()) {
            TransUser r = loadTransUser(rs);
            result.add(r);
        }
    }

    private void deleteTransUser(PreparedStatement ps, String jid) throws SQLException {
        final int jidIndex = 1;

        ps.setString(jidIndex, jid);
        ps.execute();
    }
}
