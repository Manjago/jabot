package jabot;

import jabot.chat.ChatOutQueueItem;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class Bot {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final BotConfig botConfig;
    private final BlockingQueue<ChatOutQueueItem> queue;
    private XMPPConnection connection;

    public Bot(BotConfig botConfig) {
        if (botConfig == null) {
            throw new IllegalArgumentException("bot parameters is null");
        }
        this.botConfig = new BotConfig(botConfig);
        queue = new SynchronousQueue<>();
    }

    private static String toString(Message msg) {
        if (msg == null) {
            return "null";
        }

        return "to:"
                + msg.getTo()
                + ", from:"
                + msg.getFrom()
                + ", body:"
                + msg.getBody()
                + (isDelayedMessage(msg) ? " (delayed)" : "");
    }

    private static boolean isDelayedMessage(Message msg) {
        return msg != null && msg.getExtension("delay", "urn:xmpp:delay") != null;
    }

    private static boolean isSubjectMessage(Message msg) {
        return msg != null && msg.getExtension("delay", "urn:xmpp:delay") != null;
    }

    public void start() throws XMPPException {
        ConnectionConfiguration connConfig = new ConnectionConfiguration(botConfig.getHost(), botConfig.getPort(),
                botConfig.getServiceName());

        logger.debug("botConfig {}", botConfig);

        connection = new XMPPConnection(connConfig);

        connection.connect();
        logger.info("connect ok");

        logger.debug("Connection {} {}", connection.isConnected(), connection.isAuthenticated());

        connection.login(botConfig.getLogin(), botConfig.getPassword());
        logger.info("login ok");

        logger.debug("Connection {} {}", connection.isConnected(), connection.isAuthenticated());

        Roster roster = connection.getRoster();
        roster.setSubscriptionMode(Roster.SubscriptionMode.accept_all);

        final ChatListener chatListener = new ChatListener();
        chatListener.start(queue);
        connection.addPacketListener(chatListener, new MessageTypeFilter(Message.Type.chat));

        final String room = "fido828@conference.jabber.ru";
        final String nick = "jabot";
        final String  full = room + "/" + nick;

        final MultiUserChat muc = new MultiUserChat(connection, room);
        DiscussionHistory history = new DiscussionHistory();
        history.setMaxStanzas(5);
        muc.join(nick, "", history, SmackConfiguration
                .getPacketReplyTimeout());
        muc.addMessageListener(new PacketListener() {
            @Override
            public void processPacket(Packet packet) {
                if (packet instanceof Message) {
                    Message msg = (Message) packet;
                    logger.debug(MessageFormat.format("message from {0} to {1} body {2}", msg.getFrom(), msg.getTo(), msg.getBody()));

                    if (!isDelayedMessage(msg)){
                       logger.debug("get not delayed");

                       if (msg.getSubjects().size() == 0 && !full.equals(msg.getFrom())){
                           try {
                               muc.sendMessage("Я - веселый бот, прочитал тут " + msg.getBody());
                           } catch (XMPPException e) {
                               logger.debug("fail send muc ", e);
                           }
                       }

                    }

                } else {
                    logger.debug("pkt " + packet);
                }
                logger.debug("xml " + packet.toXML());

            }
        });

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

    private void sendMessage(String to, String body) {
        if (connection == null) {
            logger.warn("connection == null");
            return;
        }
        Message msg = new Message(to, Message.Type.chat);

        msg.setBody(body);

        logger.debug("send pkt " + toString(msg));
        connection.sendPacket(msg);
    }
}
