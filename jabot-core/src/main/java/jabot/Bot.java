package jabot;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.PriorityBlockingQueue;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class Bot {

    private static final int SEC_DELAY = 1000;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final BotConfig botConfig;
    private final PriorityBlockingQueue<QueueItem> queue;
    private XMPPConnection connection;

    public Bot(BotConfig botConfig) {
        if (botConfig == null) {
            throw new IllegalArgumentException("bot parameters is null");
        }
        this.botConfig = new BotConfig(botConfig);
        queue = new PriorityBlockingQueue<>();
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

        connection.addPacketListener(new BotListener(queue), null);

        try {
            //noinspection InfiniteLoopStatement
            while (true) {

                QueueItem task = queue.peek();
                if (task == null || task.getDueDate().isAfterNow()) {
                    Thread.sleep(SEC_DELAY);
                    continue;
                }

                task = queue.poll();
                sendMessage(task.getMsgTo(), task.getMsgBody());

            }
        } catch (InterruptedException e) {
            logger.warn("interrupted", e);
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
