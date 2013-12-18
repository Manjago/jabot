package jabot;

import jabot.chat.ChatPlugin;
import jabot.room.*;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.muc.ParticipantStatusListener;
import org.jivesoftware.smackx.muc.SubjectUpdatedListener;
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
public class RoomListener implements PacketListener, SubjectUpdatedListener, ParticipantStatusListener {

    private static final int BOUND = 20;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final String meAddress;
    private List<BlockingQueue<RoomInQueueItem>> pluginsInQueue;

    public RoomListener(String meAddress) {
        if (meAddress == null) {
            throw new IllegalArgumentException("meAddress");
        }
        this.meAddress = meAddress;
    }

    public void start(Executor executor, String pluginStr, BlockingQueue<RoomOutQueueItem> queue, List<ChatPlugin> chatPlugins) {

        pluginsInQueue = new CopyOnWriteArrayList<>();

        List<BotPlugin> botPlugins = new Loader<BotPlugin>().loadPlugins(pluginStr);

        for (final BotPlugin b : botPlugins) {

            if (b instanceof RoomPlugin) {
                final RoomPlugin roomPlugin = (RoomPlugin) b;
                roomPlugin.setRoomOutQueue(queue);
                BlockingQueue<RoomInQueueItem> inQueue = new LinkedBlockingQueue<>(BOUND);
                roomPlugin.setRoomInQueue(inQueue);
                pluginsInQueue.add(inQueue);
            }

            // запускаем только если это не ChatPlugin - его мы запустим потом
            if (b instanceof ChatPlugin) {
                if (chatPlugins != null) {
                    chatPlugins.add((ChatPlugin) b);
                }
            } else {
                b.setExecutor(executor);
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            b.start();
                        } catch (Exception e) {
                            logger.error("Plugin {} error", b, e);
                        }
                    }
                });
            }

        }
    }

    @Override
    public void processPacket(Packet packet) {
        if (packet instanceof Message) {
            Message msg = (Message) packet;
            logger.debug(MessageFormat.format("message from {0} to {1} body {2}", msg.getFrom(), msg.getTo(), msg.getBody()));

            if (
                    Helper.isEmptyStr(msg.getFrom()) ||
                            Helper.isEmptyStr(msg.getBody())) {
                return;
            }


            RoomInQueueItem item;

            if (MessageUtils.isSubjectMessage(msg)) {
                item = new RoomMessage(msg.getFrom(),
                        msg.getBody(), meAddress.equals(msg.getFrom()));
            } else if (MessageUtils.isDelayedMessage(msg)) {
                item = new RoomDelayedMessage(msg.getFrom(),
                        msg.getBody(), meAddress.equals(msg.getFrom()), MessageUtils.getDelayStamp(msg));
            } else {
                item = new RoomMessage(msg.getFrom(),
                        msg.getBody(), meAddress.equals(msg.getFrom()));
            }
            send(item);

        } else {
            logger.debug("pkt " + packet);
        }
        logger.trace("xml " + packet.toXML());
    }

    @Override
    public void subjectUpdated(String subject, String from) {
        logger.debug(MessageFormat.format("message: {0} set subject {1}", from, subject));

        if (from != null && from.contains("/")) {
            send(new RoomSubjectMessage(subject, from));
        }

    }

    private void send(RoomInQueueItem item) {
        if (item == null || pluginsInQueue == null) {
            return;
        }

        try {
            for (BlockingQueue<RoomInQueueItem> q : pluginsInQueue) {
                q.put(item);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.info("interrupted");
        }
    }

    @Override
    public void joined(String participant) {
        send(new RoomParticipantMessage(participant, RoomMessageType.JOINED));
    }

    @Override
    public void left(String participant) {
        send(new RoomParticipantMessage(participant, RoomMessageType.LEFT));
    }

    @Override
    public void kicked(String participant, String actor, String reason) {
        send(new RoomParticipantBannedMessage(participant, actor, reason, RoomMessageType.KICKED));
    }

    @Override
    public void voiceGranted(String participant) {
        send(new RoomParticipantMessage(participant, RoomMessageType.VOICE_GRANTED));
    }

    @Override
    public void voiceRevoked(String participant) {
        send(new RoomParticipantMessage(participant, RoomMessageType.VOICE_REVOKED));
    }

    @Override
    public void banned(String participant, String actor, String reason) {
        send(new RoomParticipantBannedMessage(participant, actor, reason, RoomMessageType.BANNED));
    }

    @Override
    public void membershipGranted(String participant) {
        send(new RoomParticipantMessage(participant, RoomMessageType.MEMBERSHIP_GRANTED));
    }

    @Override
    public void membershipRevoked(String participant) {
        send(new RoomParticipantMessage(participant, RoomMessageType.MEMBERSHIP_REVOKED));
    }

    @Override
    public void moderatorGranted(String participant) {
        send(new RoomParticipantMessage(participant, RoomMessageType.MODERATOR_GRANTED));
    }

    @Override
    public void moderatorRevoked(String participant) {
        send(new RoomParticipantMessage(participant, RoomMessageType.MODERATOR_REVOKED));
    }

    @Override
    public void ownershipGranted(String participant) {
        send(new RoomParticipantMessage(participant, RoomMessageType.OWNERSHIP_GRANTED));
    }

    @Override
    public void ownershipRevoked(String participant) {
        send(new RoomParticipantMessage(participant, RoomMessageType.OWNERSHIP_REVOKED));
    }

    @Override
    public void adminGranted(String participant) {
        send(new RoomParticipantMessage(participant, RoomMessageType.ADMIN_GRANTED));
    }

    @Override
    public void adminRevoked(String participant) {
        send(new RoomParticipantMessage(participant, RoomMessageType.ADMIN_REVOKED));
    }

    @Override
    public void nicknameChanged(String participant, String newNickname) {
        send(new RoomNickChangedMessage(participant, newNickname));
    }
}
