package jabot;

import jabot.room.RoomInQueueItem;
import jabot.room.RoomOutQueueItem;
import jabot.room.RoomPlugin;

import java.util.concurrent.BlockingQueue;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class Translator implements RoomPlugin {
    @Override
    public void setRoomOutQueue(BlockingQueue<RoomOutQueueItem> queue) {
        //todo implement
    }

    @Override
    public void putRoomItem(RoomInQueueItem item) throws InterruptedException {
        //todo implement
    }

    @Override
    public void start() throws InterruptedException {
        //todo implement
    }
}
