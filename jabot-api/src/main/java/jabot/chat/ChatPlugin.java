package jabot.chat;

import java.util.concurrent.BlockingQueue;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public interface ChatPlugin {
    void setOutQueue(BlockingQueue<ChatOutQueueItem> queue);
    void putItem(ChatInQueueItem item) throws InterruptedException;
    void start() throws InterruptedException;
}
