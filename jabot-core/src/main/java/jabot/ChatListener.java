package jabot;

import jabot.chat.ChatInQueueItem;
import jabot.chat.ChatOutQueueItem;
import jabot.chat.ChatPlugin;
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
public class ChatListener implements PacketListener {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private List<ChatPlugin> plugins;

    public void start(String pluginStr, BlockingQueue<ChatOutQueueItem> queue) {

        plugins = new Loader<ChatPlugin>().loadPlugins(pluginStr);

        for (final ChatPlugin p : plugins) {
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
                    for (ChatPlugin p : plugins) {
                        p.putItem(new ChatInQueueItem(msg.getFrom(), msg.getBody()));
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
