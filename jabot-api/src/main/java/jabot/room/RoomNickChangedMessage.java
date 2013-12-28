package jabot.room;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class RoomNickChangedMessage implements RoomInQueueItem {

    private final String participant;
    private final String newNick;

    public RoomNickChangedMessage(String participant, String newNick) {
        this.participant = participant;
        this.newNick = newNick;
    }

    public String getNewNick() {
        return newNick;
    }

    public String getParticipant() {
        return participant;
    }

    @Override
    public RoomMessageType getType() {
        return RoomMessageType.NICKNAME_CHANGED;
    }

    @Override
    public String display(RoomMessageFormatter fmt) {
        return fmt.nickChanged(participant, newNick);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RoomNickChangedMessage{");
        sb.append("participant='").append(participant).append('\'');
        sb.append(", newNick='").append(newNick).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
