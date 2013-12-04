package jabot;

import jabot.test.Echo2Plugin;
import jabot.test.EchoPlugin;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class BotListener implements PacketListener {

    public static final int DELAY = 200;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private List<BotPlugin> plugins = new ArrayList<>();

    public void start(BlockingQueue<OutQueueItem> queue){
        plugins.add(new EchoPlugin());
        plugins.add(new Echo2Plugin());
        for (final BotPlugin p : plugins) {
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

            if (Helper.isNonEmptyStr(msg.getFrom()) && Helper.isNonEmptyStr(msg.getBody())) {
                try {
                    for (BotPlugin p : plugins) {
                        p.putItem(new InQueueItem(msg.getFrom(), msg.getBody()));
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
