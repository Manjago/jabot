package jabot.translator.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
abstract class PatternParser {

    private final Pattern pattern;


    PatternParser(String patternStr) {
        pattern = Pattern.compile(
                patternStr, Pattern.CASE_INSENSITIVE);
    }

    OperatorCmd exec(String text) {
        Matcher m = pattern.matcher(text);
        if (m.matches()) {
            String[] args = new String[m.groupCount()];
            for (int i = 0; i < m.groupCount(); ++i) {
                args[i] = m.group(i);
            }
            return execute(args);
        }
        return null;
    }

    protected abstract OperatorCmd execute(String... args);

}
