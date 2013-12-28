package jabot.room;

import jabot.JabotException;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public interface RoomInQueueItem {
    RoomMessageType getType();
    Object display(RoomMessageFormatter fmt) throws JabotException;
}
