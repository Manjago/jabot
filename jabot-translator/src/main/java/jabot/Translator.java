package jabot;

import jabot.chat.ChatInQueueItem;
import jabot.chat.ChatOutQueueItem;
import jabot.chat.ChatPlugin;
import jabot.room.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class Translator implements RoomPlugin, ChatPlugin {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private boolean inited;
    private BlockingQueue<RoomInQueueItem> roomInQueue = new SynchronousQueue<>();
    private BlockingQueue<RoomOutQueueItem> roomOutQueue;
    private BlockingQueue<ChatInQueueItem> chatInQueue = new SynchronousQueue<>();
    private BlockingQueue<ChatOutQueueItem> chatOutQueue;
    private String addrTo;

    public Translator(String config) {
        Properties props;
        try {
            props = Helper.getProperties(config);
        } catch (JabotException e) {
            logger.error("fail load props file {}", config, e);
            return;
        }

        inited = init(props);

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
    public void putRoomItem(RoomInQueueItem item) throws InterruptedException {
        roomInQueue.put(item);
    }

    @Override
    public void setChatOutQueue(BlockingQueue<ChatOutQueueItem> queue) {
        chatOutQueue = queue;
    }

    @Override
    public void putChatItem(ChatInQueueItem item) throws InterruptedException {
        chatInQueue.put(item);
    }

    @Override
    public void start() throws InterruptedException {
        if (!inited || roomOutQueue == null || chatOutQueue == null) {
            return;
        }

        // в отдельной нити - цикл ожидания из чата
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    processChat();
                } catch (Exception e) {
                    logger.error("Chat thread error", e);
                }
            }
        }).start();

        processRoom();
    }

    private void processChat() throws InterruptedException {
        while (true) {
            ChatInQueueItem item = chatInQueue.take();
            roomOutQueue.put(new RoomOutQueueItem(item.getBody()));
        }
    }

    private void processRoom() throws InterruptedException {
        while (true) {
            RoomInQueueItem item = roomInQueue.take();

            if (item instanceof RoomParticipantMessage) {

                processParticipantMessage((RoomParticipantMessage) item);

                return;
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
        chatOutQueue.put(new ChatOutQueueItem(addrTo, s));
    }

}
