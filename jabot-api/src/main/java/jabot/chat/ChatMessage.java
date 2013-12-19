package jabot.chat;

import jabot.Helper;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public final class ChatMessage implements ChatInQueueItem {
    private final String from;
    private final String body;

    public ChatMessage(String from, String body) {
        if (Helper.isEmptyStr(from) || Helper.isEmptyStr(body)) {
            throw new IllegalArgumentException("bad args");
        }
        this.from = from;
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public String getFrom() {
        return from;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ChatInQueueItem{");
        sb.append("from='").append(from).append('\'');
        sb.append(", body='").append(body).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public ChatMessageType getType() {
        return ChatMessageType.MSG;
    }
}
