package jabot.room;

import java.text.MessageFormat;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class RoomParticipantBannedMessage implements RoomInQueueItem {

    private final String participant;
    private final String actor;
    private final String reason;
    private final RoomMessageType type;

    public RoomParticipantBannedMessage(String participant, String actor, String reason, RoomMessageType type) {
        this.participant = participant;
        this.actor = actor;
        this.reason = reason;
        this.type = type;
    }

    public String getActor() {
        return actor;
    }

    public String getReason() {
        return reason;
    }

    public String getParticipant() {
        return participant;
    }

    @Override
    public RoomMessageType getType() {
        return type;
    }

    @Override
    public String display(RoomMessageFormatter fmt) {
        switch (type){
            case KICKED:
                return fmt.kicked(participant, actor, reason);
            case BANNED:
                return fmt.banned(participant, actor, reason);
            default:
                return MessageFormat.format("Unkonown message for {0}, {1}, {2}", participant, actor, reason);
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RoomParticipantBannedMessage{");
        sb.append("participant='").append(participant).append('\'');
        sb.append(", actor='").append(actor).append('\'');
        sb.append(", reason='").append(reason).append('\'');
        sb.append(", type=").append(type);
        sb.append('}');
        return sb.toString();
    }
}
