package jabot.chat;

import jabot.BotPlugin;

import java.util.concurrent.BlockingQueue;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public interface ChatPlugin extends BotPlugin {
    void setChatOutQueue(BlockingQueue<ChatOutQueueItem> queue);

    void putChatItem(ChatInQueueItem item) throws InterruptedException;
}
