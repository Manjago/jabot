package jabot;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public final class Helper {
    private Helper() {
    }

    public static boolean isEmptyStr(String s) {
        return s == null || s.length() == 0;
    }

    public static boolean isNonEmptyStr(String s) {
        return !isEmptyStr(s);
    }


}
