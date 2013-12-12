package jabot.test;

import jabot.chat.ChatInQueueItem;
import jabot.chat.ChatOutQueueItem;
import jabot.chat.ChatPlugin;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class EchoPlugin implements ChatPlugin {

    private BlockingQueue<ChatInQueueItem> inQueue = new SynchronousQueue<>();
    private BlockingQueue<ChatOutQueueItem> outQueue;

    @Override
    public void setChatOutQueue(BlockingQueue<ChatOutQueueItem> queue) {
        outQueue = queue;
    }

    @Override
    public void putChatItem(ChatInQueueItem item) throws InterruptedException {
        inQueue.put(item);
    }

    @Override
    public void start() throws InterruptedException {
        while (true) {
            ChatInQueueItem item = inQueue.take();
            outQueue.put(new ChatOutQueueItem(item.getFrom(), item.getBody()));
        }

    }
}
