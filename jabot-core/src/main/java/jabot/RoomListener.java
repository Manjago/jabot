package jabot;

import jabot.chat.ChatPlugin;
import jabot.room.RoomInQueueItem;
import jabot.room.RoomOutQueueItem;
import jabot.room.RoomPlugin;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.muc.SubjectUpdatedListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
// todo генерик с ChatListener
public class RoomListener implements PacketListener, SubjectUpdatedListener {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final String meAddress;
    private List<RoomPlugin> plugins;

    public RoomListener(String meAddress) {
        if (meAddress == null) {
            throw new IllegalArgumentException("meAddress");
        }
        this.meAddress = meAddress;
    }

    public void start(String pluginStr, BlockingQueue<RoomOutQueueItem> queue, List<ChatPlugin> chatPlugins) {

        plugins = new ArrayList<>();

        List<BotPlugin> botPlugins = new Loader<BotPlugin>().loadPlugins(pluginStr);

        for (final BotPlugin b : botPlugins) {

            if (b instanceof RoomPlugin) {
                final RoomPlugin roomPlugin = (RoomPlugin) b;
                roomPlugin.setRoomOutQueue(queue);
                plugins.add(roomPlugin);
            }

            // запускаем только если это не ChatPlugin - его мы запустим потом
            if (b instanceof ChatPlugin) {
                if (chatPlugins != null) {
                    chatPlugins.add((ChatPlugin) b);
                }
            } else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            b.start();
                        } catch (Exception e) {
                            logger.error("Plugin {} error", b, e);
                        }
                    }
                }).start();
            }

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
                        p.putRoomItem(new RoomInQueueItem(
                                msg.getFrom(),
                                msg.getBody(),
                                MessageUtils.isDelayedMessage(msg),
                                MessageUtils.isSubjectMessage(msg),
                                meAddress.equals(msg.getFrom()),
                                MessageUtils.getDelayStamp(msg)
                        ));
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

    @Override
    public void subjectUpdated(String subject, String from) {
        logger.debug(MessageFormat.format("{0} установил субжект {1}", from, subject));

        if (plugins != null) {
            try {
                for (RoomPlugin p : plugins) {
                    p.putRoomItem(new RoomInQueueItem(
                            from,
                            subject,
                            false,
                            true,
                            meAddress.equals(from),
                            null
                    ));
                }
            } catch (InterruptedException e) {
                logger.info("interrupted", e);
            }

        }

    }
}
