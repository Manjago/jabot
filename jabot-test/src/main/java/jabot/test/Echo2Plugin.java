package jabot.test;

import jabot.chat.ChatInQueueItem;
import jabot.chat.ChatOutQueueItem;
import jabot.chat.ChatPlugin;

import java.util.concurrent.BlockingQueue;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class Echo2Plugin implements ChatPlugin {

    private BlockingQueue<ChatOutQueueItem> outQueue;

    @Override
    public void setChatOutQueue(BlockingQueue<ChatOutQueueItem> queue) {
        outQueue = queue;
    }

    @Override
    public void putChatItem(ChatInQueueItem item) throws InterruptedException {
        outQueue.put(new ChatOutQueueItem(item.getFrom(), item.getBody() + item.getBody()));
    }

    @Override
    public void start() {
    }
}

