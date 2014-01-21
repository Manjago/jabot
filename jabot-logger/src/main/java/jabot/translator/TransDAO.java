package jabot.translator;

import jabot.translator.dto.TransUser;

import java.sql.SQLException;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public interface TransDAO {
    void store(TransUser user) throws SQLException;

    TransUser get(String jid) throws SQLException;

    TransUser get(long id) throws SQLException;

    void update(TransUser user) throws SQLException;

    void delete(String jid) throws SQLException;
}
