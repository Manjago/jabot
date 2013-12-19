package jabot.chat;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class ChatPresence implements ChatInQueueItem {

    private final String status;

    public String getFrom() {
        return from;
    }

    public String getStatus() {
        return status;
    }

    private final String from;

    public ChatPresence(String status, String from) {
        this.status = status;
        this.from = from;
    }

    @Override
    public ChatMessageType getType() {
        return ChatMessageType.PRESENCE;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ChatPresence{");
        sb.append("status='").append(status).append('\'');
        sb.append(", from='").append(from).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
