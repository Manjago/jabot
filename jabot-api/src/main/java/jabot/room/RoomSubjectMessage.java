package jabot.room;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class RoomSubjectMessage implements RoomInQueueItem {
    private final String subject;
    private final String from;

    public RoomSubjectMessage(String subject, String from) {
        this.subject = subject;
        this.from = from;
    }

    public String getSubject() {
        return subject;
    }

    public String getFrom() {
        return from;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RoomSubjectMessage{");
        sb.append("subject='").append(subject).append('\'');
        sb.append(", from='").append(from).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public RoomMessageType getType() {
        return RoomMessageType.SUBJECT;
    }

    @Override
    public String display(RoomMessageFormatter fmt) {
        return fmt.setSubject(from, subject);
    }
}
