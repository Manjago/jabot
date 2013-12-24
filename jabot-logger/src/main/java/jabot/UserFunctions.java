package jabot;

import java.sql.Clob;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public final class UserFunctions {

    private UserFunctions() {
    }

    public static int findByPattern(Clob clob, String patternStr) throws SQLException {

        if (clob == null || patternStr == null){
            return 0;
        }

        Pattern pattern = Pattern.compile(patternStr, Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);

        String text = clob.getSubString(1, (int) clob.length());
        final int res = pattern.matcher(text).find() ? 1 : 0;
        System.out.println("for reg " + patternStr + " test " + text + " result " + res);
        return res;
    }
}
