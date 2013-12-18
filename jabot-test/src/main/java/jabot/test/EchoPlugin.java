package jabot.test;

import jabot.chat.ChatInQueueItem;
import jabot.chat.ChatOutQueueItem;
import jabot.chat.ChatPlugin;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class EchoPlugin implements ChatPlugin {

    private BlockingQueue<ChatInQueueItem> inQueue;
    private BlockingQueue<ChatOutQueueItem> outQueue;

    @Override
    public void setChatOutQueue(BlockingQueue<ChatOutQueueItem> queue) {
        outQueue = queue;
    }

    @Override
    public void setChatInQueue(BlockingQueue<ChatInQueueItem> queue) {
        inQueue = queue;
    }

    @Override
    public void start() throws InterruptedException {
        while (!Thread.interrupted()) {
            ChatInQueueItem item = inQueue.take();
            outQueue.put(new ChatOutQueueItem(item.getFrom(), item.getBody()));
        }

    }

    @Override
    public void setExecutor(Executor executor) {
    }
}
