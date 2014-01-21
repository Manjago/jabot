package jabot.translator;

import jabot.*;
import jabot.chat.ChatInQueueItem;
import jabot.chat.ChatMessage;
import jabot.chat.ChatOutQueueItem;
import jabot.chat.ChatPresence;
import jabot.db.Database;
import jabot.db.DatabaseFactory;
import jabot.logger.LoggerDatabaseFactoryImpl;
import jabot.room.ConfigurableRoomChatPlugin;
import jabot.room.RoomInQueueItem;
import jabot.room.RoomMessageFormatter;
import jabot.room.RoomOutQueueItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Properties;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class Translator extends ConfigurableRoomChatPlugin {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final RoomMessageFormatter fmt = new DefaultRoomMessageFormatter(new Messages());
    private volatile String addrTo;
    private Database db;
    private TransDAO dao;

    public Translator(String config) throws JabotException {
        super(config);
        logger.debug("inited");
    }

    protected boolean init(Properties props) {

        final Logger logg = LoggerFactory.getLogger(getClass());

        if (props == null) {
            logg.error("empty properties");
            return false;
        }

        addrTo = props.getProperty("addrTo");
        if (Helper.isEmptyStr(addrTo)) {
            logg.error("no address to");
            return false;
        }

        DatabaseFactory dbF = new LoggerDatabaseFactoryImpl(props.getProperty("connection"), props.getProperty("user"), props.getProperty("pwd"));
        try {
            db = dbF.create();
            dao = new TransDAOImpl(db);


        } catch (SQLException e) {
            logg.error("fail check database", e);
            return false;
        }

        return true;
    }

    @Override
    public void start() throws InterruptedException {
        if (!isInited() || getRoomOutQueue() == null || getChatOutQueue() == null || getExecutor() == null) {
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
            ChatInQueueItem item = getChatInQueue().take();


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
            getChatOutQueue().put(chatOutQueueItem);

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
        if (Helper.isEmptyStr(s)) {
            return;
        }
        final ChatOutQueueItem chatOutQueueItem = new ChatOutQueueItem(addrTo, s);
        getChatOutQueue().put(chatOutQueueItem);
        logger.debug("send to chat {}", chatOutQueueItem);
    }

}
