package jabot;

import jabot.chat.ChatInQueueItem;
import jabot.chat.ChatOutQueueItem;
import jabot.chat.ChatPlugin;
import jabot.room.RoomInQueueItem;
import jabot.room.RoomOutQueueItem;
import jabot.room.RoomPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class Translator implements RoomPlugin, ChatPlugin {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private boolean inited;
    private BlockingQueue<RoomInQueueItem> roomInQueue = new SynchronousQueue<>();
    private BlockingQueue<RoomOutQueueItem> roomOutQueue;
    private BlockingQueue<ChatInQueueItem> chatInQueue = new SynchronousQueue<>();
    private BlockingQueue<ChatOutQueueItem> chatOutQueue;
    private String addrTo;

    public Translator(String config) {
        Properties props;
        try {
            props = Helper.getProperties(config);
        } catch (JabotException e) {
            logger.error("fail load props file {}", config, e);
            return;
        }

        inited = init(props);

    }

    private boolean init(Properties props) {

        if (props == null) {
            logger.error("empty properties");
            return false;
        }

        addrTo = props.getProperty("addrTo");
        if (Helper.isEmptyStr(addrTo)) {
            logger.error("no address to");
            return false;
        }

        return true;
    }

    @Override
    public void setRoomOutQueue(BlockingQueue<RoomOutQueueItem> queue) {
        roomOutQueue = queue;
    }

    @Override
    public void putRoomItem(RoomInQueueItem item) throws InterruptedException {
        roomInQueue.put(item);
    }

    @Override
    public void setChatOutQueue(BlockingQueue<ChatOutQueueItem> queue) {
        chatOutQueue = queue;
    }

    @Override
    public void putChatItem(ChatInQueueItem item) throws InterruptedException {
        chatInQueue.put(item);
    }

    @Override
    public void start() throws InterruptedException {
        if (!inited || roomOutQueue == null || chatOutQueue == null) {
            return;
        }

        // в отдельной нити - цикл ожидания из чата
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    processChat();
                } catch (Exception e) {
                    logger.error("Chat thread error", e);
                }
            }
        }).start();

        processRoom();
    }

    private void processChat() throws InterruptedException {
        while (true) {
            ChatInQueueItem item = chatInQueue.take();
            roomOutQueue.put(new RoomOutQueueItem(item.getBody()));
        }
    }

    private void processRoom() throws InterruptedException {
        while (true) {
            RoomInQueueItem item = roomInQueue.take();

            if (item.isSubject()) {
               chatOut(MessageFormat.format("{0} установил субжект \"{1}\"", item.getFrom(), item.getBody()));
            } else {
                if (!item.isDelayed()) {
                    chatOut(item);
                }
            }


        }

    }

    private void chatOut(String s) throws InterruptedException {
        chatOutQueue.put(new ChatOutQueueItem(addrTo, s));
    }

    private void chatOut(RoomInQueueItem item) throws InterruptedException {
        chatOut(MessageFormat.format("{0}: {1}", item.getFrom(), item.getBody()));
    }

}
