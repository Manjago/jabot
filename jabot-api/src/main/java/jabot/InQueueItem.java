package jabot;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public final class InQueueItem {
    private final String from;
    private final String body;

    public InQueueItem(String from, String body) {
        if (Helper.isEmptyStr(from) || Helper.isEmptyStr(body)){
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
        final StringBuilder sb = new StringBuilder("InQueueItem{");
        sb.append("from='").append(from).append('\'');
        sb.append(", body='").append(body).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
