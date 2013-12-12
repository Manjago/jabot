package jabot;

import jabot.chat.ChatOutQueueItem;
import jabot.chat.ChatPlugin;
import jabot.room.RoomOutQueueItem;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class Bot {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final BotConfig botConfig;
    private XMPPConnection connection;

    public Bot(BotConfig botConfig) {
        if (botConfig == null) {
            throw new IllegalArgumentException("bot parameters is null");
        }
        this.botConfig = new BotConfig(botConfig);
    }

    public void start() throws XMPPException {
        ConnectionConfiguration connConfig = new ConnectionConfiguration(botConfig.getHost(), botConfig.getPort(),
                botConfig.getServiceName());

        logger.debug("botConfig {}", botConfig);

        connection = new XMPPConnection(connConfig);

        connection.connect();
        logger.info("connect ok");

        connection.login(botConfig.getLogin(), botConfig.getPassword());
        logger.info("login ok");

        Roster roster = connection.getRoster();
        roster.setSubscriptionMode(Roster.SubscriptionMode.accept_all);

        List<ChatPlugin> chatPlugins = new ArrayList<>();
        initMultiChats(chatPlugins);
        initChat(botConfig.getChatPlugins(), chatPlugins);
    }

    private void initMultiChats(List<ChatPlugin> chatPlugins) throws XMPPException {
        final int paramsCount = 3;
        String[] mucParams = botConfig.getRoomsConfig().split(",");

        for(String mucParam : mucParams){
            String[] roomParams = mucParam.split("\\|");

            if (roomParams.length != paramsCount){
                continue;
            }

            final String room = roomParams[0];
            final String nick = roomParams[1];
            final String pluginStr = roomParams[2];
            joinMultiUserChat(room, nick, pluginStr, chatPlugins);
        }


    }

    private void initChat(String pluginStr, List<ChatPlugin> chatPlugins) {
        final BlockingQueue<ChatOutQueueItem> queue = new SynchronousQueue<>();
        final ChatListener chatListener = new ChatListener();
        chatListener.start(pluginStr, queue, chatPlugins);
        connection.addPacketListener(chatListener, new MessageTypeFilter(Message.Type.chat));

        ChatOutQueueItem task = null;
        try {
            while (true) {
                task = queue.take();
                sendMessage(task.getTo(), task.getBody());
            }
        } catch (InterruptedException e) {
            logger.debug("interrupted", e);
        }
    }

    private void joinMultiUserChat(String room, String nick, String pluginStr, List<ChatPlugin> chatPlugins) throws XMPPException {
        final int maxStanzas = 5;
        final String meAddr = room + "/" + nick;

        final MultiUserChat muc = new MultiUserChat(connection, room);
        DiscussionHistory history = new DiscussionHistory();
        history.setMaxStanzas(maxStanzas);
        muc.join(nick, "", history, SmackConfiguration
                .getPacketReplyTimeout());

        final BlockingQueue<RoomOutQueueItem> queue = new SynchronousQueue<>();

        final RoomListener roomListener = new RoomListener(meAddr);
        roomListener.start(pluginStr, queue, chatPlugins);
        muc.addMessageListener(roomListener);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    RoomOutQueueItem task = null;
                    try {
                        while (true) {
                            task = queue.take();
                            muc.sendMessage(task.getBody());
                        }
                    } catch (InterruptedException e) {
                        logger.debug("interrupted", e);
                    }
                } catch (Exception e) {
                    logger.error("MultiChat error", e);
                }
            }
        }).start();


    }

    private void sendMessage(String to, String body) {
        if (connection == null) {
            logger.warn("connection == null");
            return;
        }
        Message msg = new Message(to, Message.Type.chat);

        msg.setBody(body);

        logger.debug("send pkt " + MessageUtils.toString(msg));
        connection.sendPacket(msg);
    }
}
