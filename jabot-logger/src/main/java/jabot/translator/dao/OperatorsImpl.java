package jabot.translator.dao;

import jabot.db.Database;
import jabot.translator.dto.TransUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class OperatorsImpl implements Operators {

    private final Database db;
    private final TransDAO dao;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public OperatorsImpl(Database db) {
        this.db = db;
        this.dao = new TransDAOImpl(db);
    }

    @Override
    public void close() {
        try {
            db.close();
        } catch (Exception e) {
            logger.error("fail close database", db);
        }
    }

    @Override
    public boolean isOperator(String jid) {
        try {
            TransUser user = dao.get(jid);
            return user != null && user.isEnabled();

        } catch (SQLException e) {
            logger.error("fail isOperator {}", jid, e);
            return false;
        }
    }

    @Override
    public List<String> getOperators() {
        List<String> result = new ArrayList<>();
        try {
            List<TransUser> users = dao.getAll();
            for (TransUser user : users) {
                if (user.isEnabled()) {
                    result.add(user.getJid());
                }
            }

        } catch (SQLException e) {
            logger.error("fail getOperators", e);
        }
        return result;
    }

    @Override
    public List<String> getAll() {
        List<String> result = new ArrayList<>();
        try {
            List<TransUser> users = dao.getAll();
            for (TransUser user : users) {
                result.add(MessageFormat.format("{0}:{1}", user.getJid(), user.isEnabled()));
            }

        } catch (SQLException e) {
            logger.error("fail getOperators", e);
        }
        return result;
    }

    @Override
    public TransUser createIfAbsent(String jid, boolean enabled) {
        try {
            TransUser user = dao.get(jid);
            if (user == null) {
                user = new TransUser();
                user.setJid(jid);
                user.setEnabled(enabled);
                dao.store(user);
                user = dao.get(jid);
            }

            return user;

        } catch (SQLException e) {
            logger.error("fail createIfAbsent {}", MessageFormat.format("{0} {1}", jid, enabled), e);
            return null;
        }

    }

    @Override
    public void deleteIfExists(String jid) {
        try {
            dao.delete(jid);
        } catch (SQLException e) {
            logger.error("fail deleteIfExists {}", jid, e);
        }

    }

    @Override
    public TransUser updateIfExists(String jid, boolean enabled) {
        try {
            TransUser user = dao.get(jid);
            if (user != null) {
                user.setJid(jid);
                user.setEnabled(enabled);
                dao.update(user);
                user = dao.get(user.getId());
            }

            return user;

        } catch (SQLException e) {
            logger.error("fail updateIfExists {}", MessageFormat.format("{0} {1}", jid, enabled), e);
            return null;
        }

    }
}
