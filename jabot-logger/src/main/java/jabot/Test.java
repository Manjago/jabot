package jabot;

import java.sql.Clob;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class Test {
    public static void main(String[] args) {
        Pattern s = Pattern.compile("st", Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
        Matcher m =  s.matcher("satas");
        System.out.println(m.find());
    }

}
