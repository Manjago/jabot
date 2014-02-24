package jabot.translator;

import jabot.*;
import jabot.chat.ChatInQueueItem;
import jabot.chat.ChatMessage;
import jabot.chat.ChatOutQueueItem;
import jabot.chat.ChatPresence;
import jabot.db.Database;
import jabot.db.DatabaseFactory;
import jabot.room.ConfigurableRoomChatPlugin;
import jabot.room.RoomInQueueItem;
import jabot.room.RoomMessageFormatter;
import jabot.room.RoomOutQueueItem;
import jabot.translator.commands.CommandParser;
import jabot.translator.commands.CommandParserImpl;
import jabot.translator.commands.OperatorCmd;
import jabot.translator.dao.Operators;
import jabot.translator.dao.OperatorsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Properties;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class Translator extends ConfigurableRoomChatPlugin {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final RoomMessageFormatter fmt = new DefaultRoomMessageFormatter(new Messages());
    private String admin;
    private Operators transusers;
    private CommandParser commandParser;

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

        admin = props.getProperty("admin");
        if (Helper.isEmptyStr(admin)) {
            logg.error("no admin");
            return false;
        } else {
            logg.debug("admin is {}", admin);
        }

        DatabaseFactory dbF = new TranslatorDatabaseFactoryImpl(props.getProperty("connection"), props.getProperty("user"), props.getProperty("pwd"));
        try {
            Database db = dbF.create();

            transusers = new OperatorsImpl(db);
            commandParser = new CommandParserImpl(transusers);

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
                    break;
                default:
                    logger.error("unknown chatMessageType {}", item.getType());
                    break;
            }


        }
    }

    private void processChatMessage(final ChatMessage chatMessage) throws InterruptedException {
        String simpleAddr;
        try {
            simpleAddr = Addr3D.fromRaw(chatMessage.getFrom()).getNameServer();
        } catch (IllegalArgumentException e) {
            logger.error("fail process message {}", chatMessage, e);
            return;
        }


        if (admin.equals(simpleAddr)) {
            logger.debug("got message {} from admin", chatMessage.getBody());
            getExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        processAdminCommand(chatMessage.getBody());
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
        } else {
            logger.debug("got message {} from nonadmin", chatMessage.getBody());
        }

        if (transusers.isOperator(simpleAddr)) {
            final RoomOutQueueItem outQueueItem = new RoomOutQueueItem(chatMessage.getBody());
            getRoomOutQueue().put(outQueueItem);
            logger.debug("send room item {}", outQueueItem);
        } else {
            logger.debug("skip message from wrong address {}", simpleAddr);
        }
    }

    private void processAdminCommand(String body) throws InterruptedException {
        OperatorCmd cmd = commandParser.parse(body);
        if (cmd != null) {
            String userMessage = cmd.execute();
            if (Helper.isNonEmptyStr(userMessage)) {
                chatOut(admin, userMessage);
            }
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
        List<String> jids = transusers.getOperators();
        for (String jid : jids) {
            chatOut(jid, s);
        }
    }

    private void chatOut(String jid, String message) throws InterruptedException {
        final ChatOutQueueItem chatOutQueueItem = new ChatOutQueueItem(jid, message);
        getChatOutQueue().put(chatOutQueueItem);
        logger.debug("send to chat {}", chatOutQueueItem);
    }

}
