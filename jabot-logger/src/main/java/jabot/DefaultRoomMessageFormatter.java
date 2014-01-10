package jabot;

import jabot.room.RoomMessageFormatter;

import java.util.Date;

public class DefaultRoomMessageFormatter implements RoomMessageFormatter {

    private final Messages messages;

    public DefaultRoomMessageFormatter(Messages messages) {
        this.messages = messages;
    }

    @Override
    public String message(Date rcvDate, String from, String body, boolean fromMe) {
        return messages.format("msg", from, body);
    }

    @Override
    public String subjectMessageOnStart(Date rcvDate, String from, String body) {
        return body;
    }

    @Override
    public String delayedMessage(Date rcvDate, String from, String body, boolean fromMe, Date timestamp) {
        return "";
    }

    @Override
    public String setSubject(Date rcvDate, String from, String subject) {
        return messages.format(Messages.SET_SUBJECT, from, subject);
    }

    @Override
    public String kicked(Date rcvDate, String participant, String actor, String reason) {
        return messages.format(Messages.KICKED, participant, reason);
    }

    @Override
    public String banned(Date rcvDate, String participant, String actor, String reason) {
        return messages.format(Messages.BANNED, participant, reason);
    }

    @Override
    public String nickChanged(Date rcvDate, String oldNick, String newNick) {
        return messages.format(Messages.NICK_CHANGED, oldNick, newNick);
    }

    @Override
    public String joined(Date rcvDate, String participant) {
        return messages.format(Messages.JOINED, participant);
    }

    @Override
    public String left(Date rcvDate, String participant) {
        return messages.format(Messages.LEFT, participant);
    }

    @Override
    public String voiceGranted(Date rcvDate, String participant) {
        return messages.format(Messages.VOICE_GRANTED, participant);
    }

    @Override
    public String voiceRevoked(Date rcvDate, String participant) {
        return messages.format(Messages.VOICE_REVOKED, participant);
    }

    @Override
    public String memberGranted(Date rcvDate, String participant) {
        return messages.format(Messages.MEMBER_GRANTED, participant);
    }

    @Override
    public String memberRevoked(Date rcvDate, String participant) {
        return messages.format(Messages.MEMBER_REVOKED, participant);
    }

    @Override
    public String moderGranted(Date rcvDate, String participant) {
        return messages.format(Messages.MODER_GRANTED, participant);
    }

    @Override
    public String moderRevoked(Date rcvDate, String participant) {
        return messages.format(Messages.MODER_REVOKED, participant);
    }

    @Override
    public String ownerGranted(Date rcvDate, String participant) {
        return messages.format(Messages.OWNER_GRANTED, participant);
    }

    @Override
    public String ownerRevoked(Date rcvDate, String participant) {
        return messages.format(Messages.OWNER_REVOKED, participant);
    }

    @Override
    public String adminGranted(Date rcvDate, String participant) {
        return messages.format(Messages.ADMIN_GRANTED, participant);
    }

    @Override
    public String adminRevoked(Date rcvDate, String participant) {
        return messages.format(Messages.ADMIN_REVOKED, participant);
    }
}
