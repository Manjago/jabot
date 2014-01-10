package jabot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.Properties;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class Messages {

    public static final String SET_SUBJECT = "setSubject";
    public static final String KICKED = "kicked";
    public static final String BANNED = "banned";
    public static final String NICK_CHANGED = "nickChanged";
    public static final String JOINED = "joined";
    public static final String LEFT = "left";
    public static final String VOICE_GRANTED = "voiceGranted";
    public static final String VOICE_REVOKED = "voiceRevoked";
    public static final String MEMBER_GRANTED = "memberGranted";
    public static final String MEMBER_REVOKED = "memberRevoked";
    public static final String MODER_GRANTED = "moderGranted";
    public static final String MODER_REVOKED = "moderRevoked";
    public static final String OWNER_GRANTED = "ownerGranted";
    public static final String OWNER_REVOKED = "ownerRevoked";
    public static final String ADMIN_GRANTED = "adminGranted";
    public static final String ADMIN_REVOKED = "adminRevoked";
    private final Properties props;

    public Messages() {
        props = new Properties();
        try {
            try (InputStreamReader reader = new InputStreamReader(getClass().getResourceAsStream("messages.properties"), Charset.forName("UTF8"))) {
                props.load(reader);
            }
        } catch (IOException e) {
            Logger logger = LoggerFactory.getLogger(getClass());
            logger.error("fail load resource", e);
        }
    }

    public String format(String patternName, Object... arguments) {
        String pattern = props.getProperty(patternName);
        if (pattern == null) {
            return "";
        }

        return MessageFormat.format(pattern, arguments);
    }
}
