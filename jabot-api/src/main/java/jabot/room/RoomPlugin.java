package jabot.room;

import jabot.BotPlugin;

import java.util.concurrent.BlockingQueue;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public interface RoomPlugin extends BotPlugin {
    void setRoomOutQueue(BlockingQueue<RoomOutQueueItem> queue);
    void setRoomInQueue(BlockingQueue<RoomInQueueItem> queue);
}
