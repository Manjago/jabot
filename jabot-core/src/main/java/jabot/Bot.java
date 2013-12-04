package jabot;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class Bot {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final BotConfig botConfig;
    private final BlockingQueue<OutQueueItem> queue;
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

        final BotListener botListener = new BotListener();
        botListener.start(queue);
        connection.addPacketListener(botListener, null);

        OutQueueItem task = null;
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
