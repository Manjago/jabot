package jabot;

import jabot.room.RoomInQueueItem;
import jabot.room.RoomOutQueueItem;
import jabot.room.RoomPlugin;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
// todo генерик с ChatListener
public class RoomListener implements PacketListener {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final String meAddress;
    private List<RoomPlugin> plugins;

    public RoomListener(String meAddress) {
        if (meAddress == null) {
            throw new IllegalArgumentException("meAddress");
        }
        this.meAddress = meAddress;
    }

    public void start(String pluginStr, BlockingQueue<RoomOutQueueItem> queue) {
        plugins = new Loader<RoomPlugin>().loadPlugins(pluginStr);
        for (final RoomPlugin p : plugins) {
            p.setOutQueue(queue);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        p.start();
                    } catch (Exception e) {
                        logger.error("Plugin {} error", p, e);
                    }
                }
            }).start();

        }


    }

    @Override
    public void processPacket(Packet packet) {
        if (packet instanceof Message) {
            Message msg = (Message) packet;
            logger.debug(MessageFormat.format("message from {0} to {1} body {2}", msg.getFrom(), msg.getTo(), msg.getBody()));

            if (plugins != null && Helper.isNonEmptyStr(msg.getFrom()) && Helper.isNonEmptyStr(msg.getBody())) {
                try {
                    for (RoomPlugin p : plugins) {
                        p.putItem(new RoomInQueueItem(msg.getFrom(), msg.getBody(), MessageUtils.isDelayedMessage(msg),
                                MessageUtils.isSubjectMessage(msg), meAddress.equals(msg.getFrom())));
                    }
                } catch (InterruptedException e) {
                    logger.info("interrupted", e);
                }

            }

        } else {
            logger.debug("pkt " + packet);
        }
        logger.debug("xml " + packet.toXML());
    }
}
