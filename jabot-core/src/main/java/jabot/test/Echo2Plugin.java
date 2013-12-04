package jabot.test;

import jabot.BotPlugin;
import jabot.InQueueItem;
import jabot.OutQueueItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class Echo2Plugin implements BotPlugin {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private BlockingQueue<OutQueueItem> outQueue;

    @Override
    public void setOutQueue(BlockingQueue<OutQueueItem> queue) {
        outQueue = queue;
    }

    @Override
    public void putItem(InQueueItem item) throws InterruptedException {
        try {
            outQueue.put(new OutQueueItem(item.getFrom(), item.getBody() + item.getBody()));
        } catch (InterruptedException e) {
            logger.warn("interrupted", e);
        }
    }

    @Override
    public void start() {
    }
}

