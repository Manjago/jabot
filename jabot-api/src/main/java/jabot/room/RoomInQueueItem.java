package jabot.room;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public interface RoomInQueueItem {
    RoomMessageType getType();
    String display(RoomMessageFormatter fmt);
}
