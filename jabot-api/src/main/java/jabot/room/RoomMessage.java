package jabot.room;

import jabot.Helper;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class RoomMessage implements RoomInQueueItem {
    private final String from;
    private final String body;
    private final boolean fromMe;


    public RoomMessage(String from, String body, boolean fromMe) {
        if (Helper.isEmptyStr(from) || Helper.isEmptyStr(body)) {
            throw new IllegalArgumentException("bad args");
        }
        this.from = from;
        this.body = body;
        this.fromMe = fromMe;
    }

    public String getFrom() {
        return from;
    }

    public String getBody() {
        return body;
    }

    public boolean isFromMe() {
        return fromMe;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RoomMessage{");
        sb.append("from='").append(from).append('\'');
        sb.append(", body='").append(body).append('\'');
        sb.append(", fromMe=").append(fromMe);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public RoomMessageType getType() {
        return RoomMessageType.MSG;
    }

    @Override
    public String display(RoomMessageFormatter fmt) {
        return fmt.message(from, body);
    }
}
