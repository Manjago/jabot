package jabot.room;

import jabot.Helper;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class RoomInQueueItem {
    private final String from;
    private final String body;
    private final boolean delayed;
    private final boolean subject;
    private final boolean fromMe;

    public RoomInQueueItem(String from, String body, boolean delayed, boolean subject, boolean fromMe) {
        if (Helper.isEmptyStr(from) || Helper.isEmptyStr(body)) {
            throw new IllegalArgumentException("bad args");
        }
        this.from = from;
        this.body = body;
        this.delayed = delayed;
        this.subject = subject;
        this.fromMe = fromMe;
    }

    public boolean isDelayed() {
        return delayed;
    }

    public String getFrom() {
        return from;
    }

    public String getBody() {
        return body;
    }

    public boolean isSubject() {
        return subject;
    }

    public boolean isFromMe() {
        return fromMe;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RoomInQueueItem{");
        sb.append("from='").append(from).append('\'');
        sb.append(", body='").append(body).append('\'');
        sb.append(", delayed=").append(delayed);
        sb.append(", subject=").append(subject);
        sb.append(", fromMe=").append(fromMe);
        sb.append('}');
        return sb.toString();
    }
}
