package jabot.test;

import jabot.room.RoomInQueueItem;
import jabot.room.RoomOutQueueItem;
import jabot.room.RoomPlugin;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class RoomEchoPlugin implements RoomPlugin {

    private BlockingQueue<RoomInQueueItem> inQueue = new SynchronousQueue<>();
    private BlockingQueue<RoomOutQueueItem> outQueue;

    @Override
    public void setOutQueue(BlockingQueue<RoomOutQueueItem> queue) {
        outQueue = queue;
    }

    @Override
    public void putItem(RoomInQueueItem item) throws InterruptedException {
        inQueue.put(item);
    }

    @Override
    public void start() throws InterruptedException {
        while (true) {
            RoomInQueueItem item = inQueue.take();

            if (!item.isDelayed() && !item.isFromMe() && !item.isSubject()) {
                outQueue.put(new RoomOutQueueItem("Веселый бот услышал, что \"" + item.getBody() + "\""));
            }

        }

    }

}
