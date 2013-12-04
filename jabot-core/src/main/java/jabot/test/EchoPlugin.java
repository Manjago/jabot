package jabot.test;

import jabot.BotPlugin;
import jabot.InQueueItem;
import jabot.OutQueueItem;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class EchoPlugin implements BotPlugin {

    private BlockingQueue<InQueueItem> inQueue = new SynchronousQueue<>();
    private BlockingQueue<OutQueueItem> outQueue;

    @Override
    public void setOutQueue(BlockingQueue<OutQueueItem> queue) {
        outQueue = queue;
    }

    @Override
    public void putItem(InQueueItem item) throws InterruptedException {
        inQueue.put(item);
    }

    @Override
    public void start() throws InterruptedException {
        while (true) {
            InQueueItem item = inQueue.take();
            outQueue.put(new OutQueueItem(item.getFrom(), item.getBody()));
        }

    }
}
