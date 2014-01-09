package jabot.logger.plugins;

import jabot.DefaultRoomMessageFormatter;
import jabot.logger.LameFunction;
import jabot.logger.dto.LogEntry;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public abstract class LogEntryFormatter extends DefaultRoomMessageFormatter implements LameFunction<LogEntry, String> {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("[dd.MM.yyyy hh:mm:ss]");

    @Override
    public String message(Date rcvDate, String from, String body, boolean fromMe) {
        return dateFormat.format(rcvDate) + " " + super.message(rcvDate, from, body, fromMe);
    }

    @Override
    public String delayedMessage(Date rcvDate, String from, String body, boolean fromMe, Date timestamp) {
        return super.delayedMessage(rcvDate, from, body, fromMe, timestamp);
    }

    @Override
    public String subjectMessageOnStart(Date rcvDate, String from, String body) {
        return sysEvent(rcvDate, super.subjectMessageOnStart(rcvDate, from, body));
    }

    @Override
    public String setSubject(Date rcvDate, String from, String subject) {
        return sysEvent(rcvDate, super.setSubject(rcvDate, from, subject));
    }

    private String sysEvent(Date dte, String text) {
        return dateFormat.format(dte) + " *" + text + "*";
    }

    @Override
    public String kicked(Date rcvDate, String participant, String actor, String reason) {
        return sysEvent(rcvDate, super.kicked(rcvDate, participant, actor, reason));
    }

    @Override
    public String banned(Date rcvDate, String participant, String actor, String reason) {
        return sysEvent(rcvDate, super.banned(rcvDate, participant, actor, reason));
    }

    @Override
    public String nickChanged(Date rcvDate, String oldNick, String newNick) {
        return sysEvent(rcvDate, super.nickChanged(rcvDate, oldNick, newNick));
    }

    @Override
    public String joined(Date rcvDate, String participant) {
        return sysEvent(rcvDate, super.joined(rcvDate, participant));
    }

    @Override
    public String left(Date rcvDate, String participant) {
        return sysEvent(rcvDate, super.left(rcvDate, participant));
    }

    @Override
    public String voiceGranted(Date rcvDate, String participant) {
        return sysEvent(rcvDate, super.voiceGranted(rcvDate, participant));
    }

    @Override
    public String voiceRevoked(Date rcvDate, String participant) {
        return sysEvent(rcvDate, super.voiceRevoked(rcvDate, participant));
    }

    @Override
    public String memberGranted(Date rcvDate, String participant) {
        return sysEvent(rcvDate, super.memberGranted(rcvDate, participant));
    }

    @Override
    public String memberRevoked(Date rcvDate, String participant) {
        return sysEvent(rcvDate, super.memberRevoked(rcvDate, participant));
    }

    @Override
    public String ownerGranted(Date rcvDate, String participant) {
        return sysEvent(rcvDate, super.ownerGranted(rcvDate, participant));
    }

    @Override
    public String ownerRevoked(Date rcvDate, String participant) {
        return sysEvent(rcvDate, super.ownerRevoked(rcvDate, participant));
    }

    @Override
    public String adminGranted(Date rcvDate, String participant) {
        return sysEvent(rcvDate, super.adminGranted(rcvDate, participant));
    }

    @Override
    public String adminRevoked(Date rcvDate, String participant) {
        return sysEvent(rcvDate, super.adminRevoked(rcvDate, participant));
    }

    @Override
    public String moderGranted(Date rcvDate, String participant) {
        return sysEvent(rcvDate, super.moderGranted(rcvDate, participant));
    }

    @Override
    public String moderRevoked(Date rcvDate, String participant) {
        return sysEvent(rcvDate, super.moderRevoked(rcvDate, participant));
    }
}
