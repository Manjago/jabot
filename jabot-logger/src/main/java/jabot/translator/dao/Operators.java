package jabot.translator.dao;

import java.util.List;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public interface Operators extends Transusers {
    boolean isOperator(String jid);

    List<String> getOperators();
}
