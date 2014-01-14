package jabot.room;

import jabot.JabotException;
import jabot.chat.ChatInQueueItem;
import jabot.chat.ChatOutQueueItem;
import jabot.chat.ChatPlugin;

import java.util.concurrent.BlockingQueue;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public abstract class ConfigurableRoomChatPlugin extends ConfigurableRoomPlugin implements ChatPlugin {

    private BlockingQueue<ChatInQueueItem> chatInQueue;
    private BlockingQueue<ChatOutQueueItem> chatOutQueue;

    public ConfigurableRoomChatPlugin(String config) throws JabotException {
        super(config);
    }

    protected BlockingQueue<ChatInQueueItem> getChatInQueue() {
        return chatInQueue;
    }

    @Override
    public void setChatInQueue(BlockingQueue<ChatInQueueItem> chatInQueue) {
        this.chatInQueue = chatInQueue;
    }

    protected BlockingQueue<ChatOutQueueItem> getChatOutQueue() {
        return chatOutQueue;
    }

    @Override
    public void setChatOutQueue(BlockingQueue<ChatOutQueueItem> chatOutQueue) {
        this.chatOutQueue = chatOutQueue;
    }
}
