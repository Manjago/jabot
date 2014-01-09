package jabot;

import jabot.room.RoomMessageFormatter;

import java.text.MessageFormat;
import java.util.Date;

public class DefaulRoomMessageFormatter implements RoomMessageFormatter {

    @Override
    public String message(Date rcvDate, String from, String body, boolean fromMe) {
        return MessageFormat.format("{0}: {1}", from, body);
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
        return MessageFormat.format("{0} установил(а) тему: \"{1}\"", from, subject);
    }

    @Override
    public String kicked(Date rcvDate, String participant, String actor, String reason) {
        return MessageFormat.format("{0} был выкинут товарищем {1}. Причина: {2}", participant, actor, reason);
    }

    @Override
    public String banned(Date rcvDate, String participant, String actor, String reason) {
        return MessageFormat.format("{0} был забанен товарищем {1}. Причина: {2}", participant, actor, reason);
    }

    @Override
    public String nickChanged(Date rcvDate, String oldNick, String newNick) {
        return MessageFormat.format("{0} теперь известен как {1}", oldNick, newNick);
    }

    @Override
    public String joined(Date rcvDate, String participant) {
        return MessageFormat.format("К нам явился дорогой {0}", participant);
    }

    @Override
    public String left(Date rcvDate, String participant) {
        return MessageFormat.format("{0} ушел в жестокий внешний мир", participant);
    }

    @Override
    public String voiceGranted(Date rcvDate, String participant) {
        return MessageFormat.format("{0} получил право голоса", participant);
    }

    @Override
    public String voiceRevoked(Date rcvDate, String participant) {
        return MessageFormat.format("{0} лишился права голоса", participant);
    }

    @Override
    public String memberGranted(Date rcvDate, String participant) {
        return MessageFormat.format("{0} стал полноправным членом", participant);
    }

    @Override
    public String memberRevoked(Date rcvDate, String participant) {
        return MessageFormat.format("{0} перестал быть полноправным членом, очень прискорбно", participant);
    }

    @Override
    public String moderGranted(Date rcvDate, String participant) {
        return MessageFormat.format("{0} стал мурдератором", participant);
    }

    @Override
    public String moderRevoked(Date rcvDate, String participant) {
        return MessageFormat.format("{0} перестал быть мурдератором", participant);
    }

    @Override
    public String ownerGranted(Date rcvDate, String participant) {
        return MessageFormat.format("{0} стал владельцем!", participant);
    }

    @Override
    public String ownerRevoked(Date rcvDate, String participant) {
        return MessageFormat.format("{0} перестал быть владельцем, бедолага", participant);
    }

    @Override
    public String adminGranted(Date rcvDate, String participant) {
        return MessageFormat.format("{0} стал админом", participant);
    }

    @Override
    public String adminRevoked(Date rcvDate, String participant) {
        return MessageFormat.format("{0} перестал быть админом", participant);
    }
}
