package jabot;

import org.jivesoftware.smack.packet.Message;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public final class MessageUtils {
    private MessageUtils() {
    }

    public static boolean isDelayedMessage(Message msg) {
        return msg != null && msg.getExtension("delay", "urn:xmpp:delay") != null;
    }

    public static boolean isSubjectMessage(Message msg) {
        return msg != null && msg.getSubjects().size() != 0;
    }

    public static String toString(Message msg) {
        if (msg == null) {
            return "null";
        }

        return "to:"
                + msg.getTo()
                + ", from:"
                + msg.getFrom()
                + ", body:"
                + msg.getBody()
                + (isDelayedMessage(msg) ? " (delayed)" : "");
    }



}
