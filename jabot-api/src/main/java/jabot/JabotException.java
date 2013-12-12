package jabot;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class JabotException extends Exception {
    public JabotException() {
        super();
    }

    public JabotException(String message) {
        super(message);
    }

    public JabotException(String message, Throwable cause) {
        super(message, cause);
    }
}
