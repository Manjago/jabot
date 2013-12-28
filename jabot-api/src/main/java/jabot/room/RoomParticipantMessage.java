package jabot.room;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public abstract class RoomParticipantMessage implements RoomInQueueItem {

    private final String participant;
    private final RoomMessageType type;

    public RoomParticipantMessage(String participant, RoomMessageType type) {
        this.participant = participant;
        this.type = type;
    }

    public String getParticipant() {
        return participant;
    }

    @Override
    public RoomMessageType getType() {
        return type;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RoomParticipantMessage{");
        sb.append("participant='").append(participant).append('\'');
        sb.append(", type=").append(type);
        sb.append('}');
        return sb.toString();
    }
}
