package jabot;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class BotListener implements PacketListener{

    public static final int DELAY = 200;
    private final PriorityBlockingQueue<QueueItem> queue;

    public BotListener(PriorityBlockingQueue<QueueItem> queue) {
        this.queue = queue;
    }

    private boolean isEmptyStr(String s){
        return s == null || s.length() == 0;
    }

    private boolean isNonEmptyStr(String s){
        return !isEmptyStr(s);
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Override
    public void processPacket(Packet packet) {

        if (packet instanceof Message){
            Message msg = (Message) packet;
            logger.debug(MessageFormat.format("message from {0} to {1} body {2}", msg.getFrom(), msg.getTo(), msg.getBody()));

            if (isNonEmptyStr(msg.getFrom()) && isNonEmptyStr(msg.getBody())){
                queue.put(new QueueItem(new DateTime().plusMillis(DELAY), msg.getFrom(), msg.getBody()));

            }

        } else {
            logger.debug("pkt " + packet);
        }
        logger.debug("xml " + packet.toXML());


    }
}
