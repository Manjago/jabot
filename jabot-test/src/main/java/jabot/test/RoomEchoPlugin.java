package jabot.test;

import jabot.room.*;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class RoomEchoPlugin implements RoomPlugin {

    private BlockingQueue<RoomInQueueItem> inQueue;
    private BlockingQueue<RoomOutQueueItem> outQueue;

    @Override
    public void setRoomOutQueue(BlockingQueue<RoomOutQueueItem> queue) {
        outQueue = queue;
    }

    @Override
    public void setRoomInQueue(BlockingQueue<RoomInQueueItem> queue) {
        inQueue = queue;
    }

    @Override
    public void start() throws InterruptedException {
        while (!Thread.interrupted()) {
            RoomInQueueItem item = inQueue.take();

            if (item.getType() == RoomMessageType.MSG) {
                RoomMessage msg = (RoomMessage) item;
                outQueue.put(new RoomOutQueueItem("Веселый бот услышал, что \"" + msg.getBody() + "\""));
            }


        }

    }

    @Override
    public void setExecutor(Executor executor) {
    }

}
