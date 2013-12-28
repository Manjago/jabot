package jabot.translator;

import jabot.Addr3D;
import jabot.Helper;
import jabot.JabotException;
import jabot.PluginVersion;
import jabot.chat.*;
import jabot.room.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class Translator implements RoomPlugin, ChatPlugin {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private volatile boolean inited;
    private BlockingQueue<RoomInQueueItem> roomInQueue;
    private BlockingQueue<RoomOutQueueItem> roomOutQueue;
    private BlockingQueue<ChatInQueueItem> chatInQueue;
    private BlockingQueue<ChatOutQueueItem> chatOutQueue;
    private volatile String addrTo;
    private Executor executor;

    public Translator(String config) {
        Properties props;
        try {
            props = Helper.getProperties(config);
        } catch (JabotException e) {
            logger.error("fail load props file {}", config, e);
            return;
        }

        inited = init(props);
        logger.debug("inited");

    }

    private boolean init(Properties props) {

        if (props == null) {
            logger.error("empty properties");
            return false;
        }

        addrTo = props.getProperty("addrTo");
        if (Helper.isEmptyStr(addrTo)) {
            logger.error("no address to");
            return false;
        }

        return true;
    }

    @Override
    public void setRoomOutQueue(BlockingQueue<RoomOutQueueItem> queue) {
        roomOutQueue = queue;
    }

    @Override
    public void setRoomInQueue(BlockingQueue<RoomInQueueItem> queue) {
        roomInQueue = queue;
    }

    @Override
    public void setChatOutQueue(BlockingQueue<ChatOutQueueItem> queue) {
        chatOutQueue = queue;
    }

    @Override
    public void setChatInQueue(BlockingQueue<ChatInQueueItem> queue) {
        chatInQueue = queue;
    }

    @Override
    public void start() throws InterruptedException {
        if (!inited || roomOutQueue == null || chatOutQueue == null || executor == null) {
            logger.error("not inited!");
            return;
        }

        // в отдельной нити - цикл ожидания из чата
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    processChat();
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.debug("Interrupted");
                }
                catch (Exception e) {
                    logger.error("Chat thread error", e);
                }
            }
        });

        logger.debug("inited, before room process loop");
        processRoom();
    }

    @Override
    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    @Override
    public PluginVersion getPluginVersion() {
        return new PluginVersion() {
            @Override
            public int getMajor() {
                return 1;
            }

            @Override
            public int getMinor() {
                return 0;
            }
        };
    }

    private void processChat() throws InterruptedException {
        while (!Thread.interrupted()) {
            ChatInQueueItem item = chatInQueue.take();


            switch (item.getType()) {
                case MSG:
                    logger.debug("got message item {}", item);
                    processChatMessage((ChatMessage) item);
                    break;
                case PRESENCE:
                    logger.trace("got presense item {}", item);
                    processPresence((ChatPresence) item);
                    break;
                default:
                    logger.error("unknown chatMessageType {}", item.getType());
                    break;
            }


        }
    }

    private void processChatMessage(ChatMessage chatMessage) throws InterruptedException {
        String simpleAddr;
        try {
            simpleAddr = Addr3D.fromRaw(chatMessage.getFrom()).getNameServer();
        } catch (IllegalArgumentException e) {
            logger.error("fail process message {}", chatMessage, e);
            return;
        }

        if (addrTo.equals(simpleAddr)) {
            final RoomOutQueueItem outQueueItem = new RoomOutQueueItem(chatMessage.getBody());
            roomOutQueue.put(outQueueItem);
            logger.debug("send room item {}", outQueueItem);
        } else {
            logger.debug("skip message from wrong address {}", simpleAddr);
        }
    }

    private void processPresence(ChatPresence chatMessage) throws InterruptedException {

        if (!"available".equals(chatMessage.getStatus())) {
            logger.trace("bad status {}", chatMessage.getStatus());
            return;
        }

        String simpleAddr;
        try {
            simpleAddr = Addr3D.fromRaw(chatMessage.getFrom()).getNameServer();
        } catch (IllegalArgumentException e) {
            logger.error("fail process message {}", chatMessage, e);
            return;
        }

        if (addrTo.equals(simpleAddr)) {
            final ChatOutQueueItem chatOutQueueItem = new ChatOutQueueItem(addrTo, MessageFormat.format("Привет, дружище {0}!", addrTo));
            chatOutQueue.put(chatOutQueueItem);

            logger.debug("send to chat {}", chatOutQueueItem);
        } else {
            logger.trace("skip message from not our address {}", simpleAddr);
        }
    }

    private void processRoom() throws InterruptedException {

        while (!Thread.interrupted()) {
            logger.debug("waiting room item");
            RoomInQueueItem item = roomInQueue.take();

            logger.debug("got room item {}", item);

            if (item instanceof RoomParticipantMessage) {

                processParticipantMessage((RoomParticipantMessage) item);

                continue;
            }

            switch (item.getType()) {
                case MSG:
                    RoomMessage msg = (RoomMessage) item;
                    chatOut(MessageFormat.format("{0}: {1}", msg.getFrom(), msg.getBody()));
                    break;
                case SUBJECT:
                    RoomSubjectMessage sm = (RoomSubjectMessage) item;
                    chatOut(MessageFormat.format("{0} установил субжект \"{1}\"", sm.getFrom(), sm.getSubject()));
                    break;
                case DELAYED_MSG:
                    // ignore
                    break;
                case KICKED:
                    chatOut(MessageFormat.format("{0} был выкинут товарищем {1}. Причина: {2}", ((RoomParticipantBannedMessage) item).getParticipant(), ((RoomParticipantBannedMessage) item).getActor(), ((RoomParticipantBannedMessage) item).getReason()));
                    break;
                case BANNED:
                    chatOut(MessageFormat.format("{0} был забанен товарищем {1}. Причина: {2}", ((RoomParticipantBannedMessage) item).getParticipant(), ((RoomParticipantBannedMessage) item).getActor(), ((RoomParticipantBannedMessage) item).getReason()));
                    break;
                case NICKNAME_CHANGED:
                    chatOut(MessageFormat.format("{0} теперь известен как {1}", ((RoomNickChangedMessage) item).getParticipant(), ((RoomNickChangedMessage) item).getNewNick()));
                    break;
                default:
                    logger.error("Unknown type {}", item.getType());
                    break;

            }


        }

        logger.info("leave thread");

    }

    private void processParticipantMessage(RoomParticipantMessage item) throws InterruptedException {
        switch (item.getType()) {
            case JOINED:
                chatOut(MessageFormat.format("К нам явился дорогой {0}", item.getParticipant()));
                break;
            case LEFT:
                chatOut(MessageFormat.format("{0} ушел в жестокий внешний мир", item.getParticipant()));
                break;
            case VOICE_GRANTED:
                chatOut(MessageFormat.format("{0} получил право голоса", item.getParticipant()));
                break;
            case VOICE_REVOKED:
                chatOut(MessageFormat.format("{0} лишился права голоса", item.getParticipant()));
                break;
            default:
                processRareParticipantMessage(item);
                break;

        }
    }

    private void processRareParticipantMessage(RoomParticipantMessage item) throws InterruptedException {
        switch (item.getType()) {
            case MEMBERSHIP_GRANTED:
                chatOut(MessageFormat.format("{0} стал полноправным членом", item.getParticipant()));
                break;
            case MEMBERSHIP_REVOKED:
                chatOut(MessageFormat.format("{0} перестал быть полноправным членом, очень прискорбно", item.getParticipant()));
                break;
            case MODERATOR_GRANTED:
                chatOut(MessageFormat.format("{0} стал мурдератором", item.getParticipant()));
                break;
            case MODERATOR_REVOKED:
                chatOut(MessageFormat.format("{0} перестал быть мурдератором", item.getParticipant()));
                break;
            case OWNERSHIP_GRANTED:
                chatOut(MessageFormat.format("{0} стал собственником", item.getParticipant()));
                break;
            case OWNERSHIP_REVOKED:
                chatOut(MessageFormat.format("{0} перестал быть собственником", item.getParticipant()));
                break;
            case ADMIN_GRANTED:
                chatOut(MessageFormat.format("{0} стал админом", item.getParticipant()));
                break;
            case ADMIN_REVOKED:
                chatOut(MessageFormat.format("{0} перестал быть админом", item.getParticipant()));
                break;
            default:
                logger.error("Unknown type {}", item.getType());
                break;

        }
    }

    private void chatOut(String s) throws InterruptedException {
        final ChatOutQueueItem chatOutQueueItem = new ChatOutQueueItem(addrTo, s);
        chatOutQueue.put(chatOutQueueItem);
        logger.debug("send to chat {}", chatOutQueueItem);
    }

}
