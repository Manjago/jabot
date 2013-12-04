package jabot;

import java.util.concurrent.BlockingQueue;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public interface BotPlugin {
    void setOutQueue(BlockingQueue<OutQueueItem> queue);
    void putItem(InQueueItem item) throws InterruptedException;
    void start() throws InterruptedException;
}
