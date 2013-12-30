package jabot.room;

import jabot.JabotException;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public interface RoomInQueueItem {
    Object display(RoomMessageFormatter fmt) throws JabotException;
}
