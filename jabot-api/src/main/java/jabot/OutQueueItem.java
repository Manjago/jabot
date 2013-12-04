package jabot;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public final class OutQueueItem {
    private final String to;
    private final String body;

    public OutQueueItem(String to, String body) {
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
        final StringBuilder sb = new StringBuilder("OutQueueItem{");
        sb.append("to='").append(to).append('\'');
        sb.append(", body='").append(body).append('\'');
        sb.append('}');
        return sb.toString();
    }


}
