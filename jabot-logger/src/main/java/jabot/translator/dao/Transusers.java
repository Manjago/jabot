package jabot.translator.dao;

import jabot.translator.dto.TransUser;

import java.util.List;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public interface Transusers extends AutoCloseable {
    boolean isOperator(String jid);

    List<String> getOperators();

    List<String> getAll();

    TransUser createIfAbsent(String jid, boolean enabled);

    void deleteIfExists(String jid);

    TransUser updateIfExists(String jid, boolean enabled);
}
