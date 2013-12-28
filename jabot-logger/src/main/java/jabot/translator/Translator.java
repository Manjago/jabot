package jabot.translator;

import jabot.*;
import jabot.chat.*;
import jabot.room.ConfigurableRoomPlugin;
import jabot.room.RoomInQueueItem;
import jabot.room.RoomMessageFormatter;
import jabot.room.RoomOutQueueItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class Translator extends ConfigurableRoomPlugin implements ChatPlugin {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final RoomMessageFormatter fmt = new DefaulRoomMessageFormatter();
    private BlockingQueue<ChatInQueueItem> chatInQueue;
    private BlockingQueue<ChatOutQueueItem> chatOutQueue;
    private volatile String addrTo;

    public Translator(String config) throws JabotException {
        super(config);
        logger.debug("inited");
    }

    protected boolean init(Properties props) {

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
    public void setChatOutQueue(BlockingQueue<ChatOutQueueItem> queue) {
        chatOutQueue = queue;
    }

    @Override
    public void setChatInQueue(BlockingQueue<ChatInQueueItem> queue) {
        chatInQueue = queue;
    }

    @Override
    public void start() throws InterruptedException {
        if (!isInited() || getRoomOutQueue() == null || chatOutQueue == null || getExecutor() == null) {
            logger.error("not inited!");
            return;
        }

        // в отдельной нити - цикл ожидания из чата
        getExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    processChat();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.debug("Interrupted");
                } catch (Exception e) {
                    logger.error("Chat thread error", e);
                }
            }
        });

        logger.debug("inited, before room process loop");
        processRoom();
    }

    @Override
    public PluginVersion getPluginVersion() {
        return new PluginVersion() {
            @Override
            public int getMajor() {
                return 1;
            }

            @Override
            public int getMinor() {
                return 0;
            }
        };
    }

    private void processChat() throws InterruptedException {
        while (!Thread.interrupted()) {
            ChatInQueueItem item = chatInQueue.take();


            switch (item.getType()) {
                case MSG:
                    logger.debug("got message item {}", item);
                    processChatMessage((ChatMessage) item);
                    break;
                case PRESENCE:
                    logger.trace("got presense item {}", item);
                    processPresence((ChatPresence) item);
                    break;
                default:
                    logger.error("unknown chatMessageType {}", item.getType());
                    break;
            }


        }
    }

    private void processChatMessage(ChatMessage chatMessage) throws InterruptedException {
        String simpleAddr;
        try {
            simpleAddr = Addr3D.fromRaw(chatMessage.getFrom()).getNameServer();
        } catch (IllegalArgumentException e) {
            logger.error("fail process message {}", chatMessage, e);
            return;
        }

        if (addrTo.equals(simpleAddr)) {
            final RoomOutQueueItem outQueueItem = new RoomOutQueueItem(chatMessage.getBody());
            getRoomOutQueue().put(outQueueItem);
            logger.debug("send room item {}", outQueueItem);
        } else {
            logger.debug("skip message from wrong address {}", simpleAddr);
        }
    }

    private void processPresence(ChatPresence chatMessage) throws InterruptedException {

        if (!"available".equals(chatMessage.getStatus())) {
            logger.trace("bad status {}", chatMessage.getStatus());
            return;
        }

        String simpleAddr;
        try {
            simpleAddr = Addr3D.fromRaw(chatMessage.getFrom()).getNameServer();
        } catch (IllegalArgumentException e) {
            logger.error("fail process message {}", chatMessage, e);
            return;
        }

        if (addrTo.equals(simpleAddr)) {
            final ChatOutQueueItem chatOutQueueItem = new ChatOutQueueItem(addrTo, MessageFormat.format("Привет, дружище {0}!", addrTo));
            chatOutQueue.put(chatOutQueueItem);

            logger.debug("send to chat {}", chatOutQueueItem);
        } else {
            logger.trace("skip message from not our address {}", simpleAddr);
        }
    }

    private void processRoom() throws InterruptedException {

        while (!Thread.interrupted()) {
            logger.debug("waiting room item");
            RoomInQueueItem item = getRoomInQueue().take();

            logger.debug("got room item {}", item);
            try {
                chatOut(String.valueOf(item.display(fmt)));
            } catch (JabotException e) {
                logger.error("fail diplay item {}, ignored", item, e);
            }
        }

        logger.info("leave thread");

    }

    private void chatOut(String s) throws InterruptedException {
        final ChatOutQueueItem chatOutQueueItem = new ChatOutQueueItem(addrTo, s);
        chatOutQueue.put(chatOutQueueItem);
        logger.debug("send to chat {}", chatOutQueueItem);
    }

}
