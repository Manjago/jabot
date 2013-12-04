package jabot;

import org.joda.time.DateTime;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public final class QueueItem implements Comparable<QueueItem> {
    private final String msgTo;
    private final DateTime dueDate;
    private final String msgBody;

    public QueueItem(DateTime dueDate, String msgTo, String msgBody) {
        if (dueDate == null) {
            throw new IllegalArgumentException("dueDate is null");
        }
        this.msgTo = msgTo;
        this.dueDate = dueDate;
        this.msgBody = msgBody;
    }

    public DateTime getDueDate() {
        return dueDate;
    }

    public String getMsgBody() {
        return msgBody;
    }

    public String getMsgTo() {
        return msgTo;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("QueueItem{");
        sb.append("msgTo='").append(msgTo).append('\'');
        sb.append(", dueDate=").append(dueDate);
        sb.append(", msgBody='").append(msgBody).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int compareTo(QueueItem o) {
        int res = getDueDate().compareTo(o.getDueDate());

        if (res != 0) {
            return res;
        }

        res = String.valueOf(getMsgTo()).compareTo(String.valueOf(o.getMsgTo()));

        if (res != 0) {
            return res;
        }

        return String.valueOf(getMsgBody()).compareTo(String.valueOf(o.getMsgBody()));

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        return equalsConcrete((QueueItem) o);
    }

    private boolean equalsConcrete(QueueItem queueItem) {
        if (!dueDate.equals(queueItem.dueDate)) {
            return false;
        }
        if (msgBody != null ? !msgBody.equals(queueItem.msgBody) : queueItem.msgBody != null) {
            return false;
        }
        if (msgTo != null ? !msgTo.equals(queueItem.msgTo) : queueItem.msgTo != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = msgTo != null ? msgTo.hashCode() : 0;
        final int prime = 31;
        result = prime * result + dueDate.hashCode();
        result = prime * result + (msgBody != null ? msgBody.hashCode() : 0);
        return result;
    }
}
