package jabot;

import jabot.chat.*;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class ChatListener implements PacketListener {

    public static final int BOUND = 20;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private List<BlockingQueue<ChatInQueueItem>> pluginInQueues;

    public void start(ExecutorProvider executorProvider, String pluginStr, BlockingQueue<ChatOutQueueItem> queue, List<ChatPlugin> chatPlugins) {

        List<ChatPlugin> plugins = new Loader<ChatPlugin>().loadPlugins(pluginStr);
        pluginInQueues = new CopyOnWriteArrayList<>();

        if (chatPlugins != null) {
            for (ChatPlugin chatPlugin : chatPlugins) {
                logger.debug("loaded plugin {}", Helper.displayPlugin(chatPlugin));
                final LinkedBlockingQueue<ChatInQueueItem> q = new LinkedBlockingQueue<>(BOUND);
                pluginInQueues.add(q);
                chatPlugin.setChatInQueue(q);
                plugins.add(chatPlugin);
                logger.debug("plugin {} inited as chatPlugin, queue {}", Helper.displayPlugin(chatPlugin), q);
            }
        }

        for (final ChatPlugin p : plugins) {
            p.setChatOutQueue(queue);
            p.setExecutorProvider(executorProvider);
            executorProvider.getExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        logger.debug("plugin {} started", Helper.displayPlugin(p));
                        p.start();
                    } catch (InterruptedException e) {
                        logger.debug("interrupted");
                        Thread.currentThread().interrupt();
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
            String debugMsg = MessageFormat.format("message from {0} to {1} body {2}", msg.getFrom(), msg.getTo(), msg.getBody());
            if (msg.getBody() == null) {
                logger.trace(debugMsg);
            } else {
                logger.debug(debugMsg);
            }


            if (pluginInQueues != null && Helper.isNonEmptyStr(msg.getFrom()) && Helper.isNonEmptyStr(msg.getBody())) {
                final ChatMessage chatMessage = new ChatMessage(msg.getFrom(), msg.getBody());
                for (BlockingQueue<ChatInQueueItem> q : pluginInQueues) {
                    q.add(chatMessage);
                }
            }

        } else if (packet instanceof Presence) {
            Presence p = (Presence) packet;
            logger.trace("presence {} {}", p.getType(), p.getFrom());

            if (pluginInQueues != null) {
                final ChatPresence chatPresence = new ChatPresence(p.getType().name(), p.getFrom());
                for (BlockingQueue<ChatInQueueItem> q : pluginInQueues) {
                    q.add(chatPresence);
                }
            }
        } else {
            logger.debug("pkt " + packet);
        }
        logger.trace("xml " + packet.toXML());


    }
}
