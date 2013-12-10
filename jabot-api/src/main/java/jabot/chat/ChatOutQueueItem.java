package jabot.chat;

import jabot.Helper;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public final class ChatOutQueueItem {
    private final String to;
    private final String body;

    public ChatOutQueueItem(String to, String body) {
        if (Helper.isEmptyStr(to) || Helper.isEmptyStr(body)) {
            throw new IllegalArgumentException("bad args");
        }
        this.to = to;
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public String getTo() {
        return to;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ChatOutQueueItem{");
        sb.append("to='").append(to).append('\'');
        sb.append(", body='").append(body).append('\'');
        sb.append('}');
        return sb.toString();
    }


}
