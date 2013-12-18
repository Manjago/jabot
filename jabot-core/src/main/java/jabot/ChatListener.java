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
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class ChatListener implements PacketListener {

    public static final int BOUND = 20;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private List<BlockingQueue<ChatInQueueItem>> pluginInQueues;

    public void start(Executor executor, String pluginStr, BlockingQueue<ChatOutQueueItem> queue, List<ChatPlugin> chatPlugins) {

        List<ChatPlugin> plugins = new Loader<ChatPlugin>().loadPlugins(pluginStr);
        pluginInQueues = new CopyOnWriteArrayList<>();

        if (chatPlugins != null) {
            for (ChatPlugin chatPlugin : chatPlugins) {
                final LinkedBlockingQueue<ChatInQueueItem> q = new LinkedBlockingQueue<>(BOUND);
                pluginInQueues.add(q);
                chatPlugin.setChatInQueue(q);
                plugins.add(chatPlugin);
            }
        }

        for (final ChatPlugin p : plugins) {
            p.setChatOutQueue(queue);
            p.setExecutor(executor);
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        p.start();
                    } catch (Exception e) {
                        logger.error("Plugin {} error", p, e);
                    }
                }
            });

        }


    }

    @Override
    public void processPacket(Packet packet) {

        if (packet instanceof Message) {
            Message msg = (Message) packet;
            logger.debug(MessageFormat.format("message from {0} to {1} body {2}", msg.getFrom(), msg.getTo(), msg.getBody()));

            if (pluginInQueues != null && Helper.isNonEmptyStr(msg.getFrom()) && Helper.isNonEmptyStr(msg.getBody())) {
                for (BlockingQueue<ChatInQueueItem> q : pluginInQueues) {
                    q.add(new ChatInQueueItem(msg.getFrom(), msg.getBody()));
                }
            }

        } else {
            logger.debug("pkt " + packet);
        }
        logger.trace("xml " + packet.toXML());


    }
}
