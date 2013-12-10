package jabot.room;

import jabot.Helper;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class RoomOutQueueItem {
    private final String body;

    public RoomOutQueueItem(String body) {
        if (Helper.isEmptyStr(body)) {
            throw new IllegalArgumentException("bad args");
        }
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RoomOutQueueItem{");
        sb.append("body='").append(body).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
