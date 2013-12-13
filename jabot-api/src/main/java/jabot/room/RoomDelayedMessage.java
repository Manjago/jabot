package jabot.room;

import java.util.Date;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class RoomDelayedMessage extends RoomMessage {

    private final Date delayStamp;

    public RoomDelayedMessage(String from, String body, boolean fromMe, Date delayStamp) {
        super(from, body, fromMe);

        this.delayStamp = delayStamp != null ? new Date(delayStamp.getTime()) : null;
    }

    public Date getDelayStamp() {
        return delayStamp != null ? new Date(delayStamp.getTime()) : null;
    }

    @Override
    public RoomMessageType getType() {
        return RoomMessageType.DELAYED_MSG;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RoomDelayedMessage{");
        sb.append("delayStamp=").append(delayStamp);
        sb.append("super=").append(super.toString());
        sb.append('}');
        return sb.toString();
    }
}
