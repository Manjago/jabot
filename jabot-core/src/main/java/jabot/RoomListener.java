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

            logger.debug("loaded plugin {}", Helper.displayPlugin(b));

            if (b instanceof RoomPlugin) {
                final RoomPlugin roomPlugin = (RoomPlugin) b;
                roomPlugin.setRoomOutQueue(queue);
                BlockingQueue<RoomInQueueItem> inQueue = new LinkedBlockingQueue<>(BOUND);
                roomPlugin.setRoomInQueue(inQueue);
                pluginsInQueue.add(inQueue);
                logger.debug("plugin {} inited as roomPlugin, queue {}", Helper.displayPlugin(b), inQueue);
            }

            // запускаем только если это не ChatPlugin - его мы запустим потом
            if (b instanceof ChatPlugin) {
                if (chatPlugins != null) {
                    chatPlugins.add((ChatPlugin) b);
                    logger.debug("plugin {} deffered as chatPlugin", Helper.displayPlugin(b));
                }
            } else {
                b.setExecutor(executor);
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            logger.debug("plugin {} started", Helper.displayPlugin(b));
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
                logger.debug("put roomItem {} to queue {}", item, q);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.info("interrupted");
        }
    }

    @Override
    public void joined(final String participant) {
        send(new RoomParticipantMessage(participant, RoomMessageType.JOINED){
            @Override
            public String display(RoomMessageFormatter fmt) {
                return fmt.joined(participant);
            }
        });
    }

    @Override
    public void left(final String participant) {
        send(new RoomParticipantMessage(participant, RoomMessageType.LEFT){
            @Override
            public String display(RoomMessageFormatter fmt) {
                return fmt.left(participant);
            }
        });
    }

    @Override
    public void kicked(String participant, String actor, String reason) {
        send(new RoomParticipantBannedMessage(participant, actor, reason, RoomMessageType.KICKED));
    }

    @Override
    public void voiceGranted(final String participant) {
        send(new RoomParticipantMessage(participant, RoomMessageType.VOICE_GRANTED){
            @Override
            public String display(RoomMessageFormatter fmt) {
                return fmt.voiceGranted(participant);
            }
        });
    }

    @Override
    public void voiceRevoked(final String participant) {
        send(new RoomParticipantMessage(participant, RoomMessageType.VOICE_REVOKED){
            @Override
            public String display(RoomMessageFormatter fmt) {
                return fmt.voiceRevoked(participant);
            }
        });
    }

    @Override
    public void banned(String participant, String actor, String reason) {
        send(new RoomParticipantBannedMessage(participant, actor, reason, RoomMessageType.BANNED));
    }

    @Override
    public void membershipGranted(final String participant) {
        send(new RoomParticipantMessage(participant, RoomMessageType.MEMBERSHIP_GRANTED){
            @Override
            public String display(RoomMessageFormatter fmt) {
                return fmt.memberGranted(participant);
            }
        });
    }

    @Override
    public void membershipRevoked(final String participant) {
        send(new RoomParticipantMessage(participant, RoomMessageType.MEMBERSHIP_REVOKED){
            @Override
            public String display(RoomMessageFormatter fmt) {
                return fmt.memberRevoked(participant);
            }
        });
    }

    @Override
    public void moderatorGranted(final String participant) {
        send(new RoomParticipantMessage(participant, RoomMessageType.MODERATOR_GRANTED){
            @Override
            public String display(RoomMessageFormatter fmt) {
                return fmt.moderGranted(participant);
            }
        });
    }

    @Override
    public void moderatorRevoked(final String participant) {
        send(new RoomParticipantMessage(participant, RoomMessageType.MODERATOR_REVOKED){
            @Override
            public String display(RoomMessageFormatter fmt) {
                return fmt.moderRevoked(participant);
            }
        });
    }

    @Override
    public void ownershipGranted(final String participant) {
        send(new RoomParticipantMessage(participant, RoomMessageType.OWNERSHIP_GRANTED){
            @Override
            public String display(RoomMessageFormatter fmt) {
                return fmt.ownerGranted(participant);
            }
        });
    }

    @Override
    public void ownershipRevoked(final String participant) {
        send(new RoomParticipantMessage(participant, RoomMessageType.OWNERSHIP_REVOKED){
            @Override
            public String display(RoomMessageFormatter fmt) {
                return fmt.ownerRevoked(participant);
            }
        });
    }

    @Override
    public void adminGranted(final String participant) {
        send(new RoomParticipantMessage(participant, RoomMessageType.ADMIN_GRANTED){
            @Override
            public String display(RoomMessageFormatter fmt) {
                return fmt.adminGranted(participant);
            }
        });
    }

    @Override
    public void adminRevoked(final String participant) {
        send(new RoomParticipantMessage(participant, RoomMessageType.ADMIN_REVOKED){
            @Override
            public String display(RoomMessageFormatter fmt) {
                return fmt.adminRevoked(participant);
            }
        });
    }

    @Override
    public void nicknameChanged(String participant, String newNickname) {
        send(new RoomNickChangedMessage(participant, newNickname));
    }
}
