package jabot.test;

import jabot.BotPlugin;
import jabot.InQueueItem;
import jabot.OutQueueItem;

import java.util.concurrent.BlockingQueue;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class Echo2Plugin implements BotPlugin {

    private BlockingQueue<OutQueueItem> outQueue;

    @Override
    public void setOutQueue(BlockingQueue<OutQueueItem> queue) {
        outQueue = queue;
    }

    @Override
    public void putItem(InQueueItem item) throws InterruptedException {
        outQueue.put(new OutQueueItem(item.getFrom(), item.getBody() + item.getBody()));
    }

    @Override
    public void start() {
    }
}

