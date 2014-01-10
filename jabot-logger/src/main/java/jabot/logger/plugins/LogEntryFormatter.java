package jabot.logger.plugins;

import jabot.logger.LameFunction;
import jabot.logger.dto.LogEntry;
import jabot.room.RoomMessageFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public abstract class LogEntryFormatter implements LameFunction<LogEntry, String> {

    private final SimpleDateFormat dateFormat;
    private final RoomMessageFormatter frm;

    protected LogEntryFormatter(SimpleDateFormat dateFormat, RoomMessageFormatter frm) {
        this.dateFormat = dateFormat;
        this.frm = frm;
    }

    public String message(Date rcvDate, String from, String body, boolean fromMe) {
        return dateFormat.format(rcvDate) + " " + frm.message(rcvDate, from, body, fromMe);
    }

    public String subjectMessageOnStart(Date rcvDate, String from, String body) {
        return sysEvent(rcvDate, frm.subjectMessageOnStart(rcvDate, from, body));
    }

    public String setSubject(Date rcvDate, String from, String subject) {
        return sysEvent(rcvDate, frm.setSubject(rcvDate, from, subject));
    }

    private String sysEvent(Date dte, Object text) {
        return dateFormat.format(dte) + " *" + text + "*";
    }

    public String kicked(Date rcvDate, String participant, String actor, String reason) {
        return sysEvent(rcvDate, frm.kicked(rcvDate, participant, actor, reason));
    }

    public String banned(Date rcvDate, String participant, String actor, String reason) {
        return sysEvent(rcvDate, frm.banned(rcvDate, participant, actor, reason));
    }

    public String nickChanged(Date rcvDate, String oldNick, String newNick) {
        return sysEvent(rcvDate, frm.nickChanged(rcvDate, oldNick, newNick));
    }

    public String joined(Date rcvDate, String participant) {
        return sysEvent(rcvDate, frm.joined(rcvDate, participant));
    }

    public String left(Date rcvDate, String participant) {
        return sysEvent(rcvDate, frm.left(rcvDate, participant));
    }

    public String voiceGranted(Date rcvDate, String participant) {
        return sysEvent(rcvDate, frm.voiceGranted(rcvDate, participant));
    }

    public String voiceRevoked(Date rcvDate, String participant) {
        return sysEvent(rcvDate, frm.voiceRevoked(rcvDate, participant));
    }

    public String memberGranted(Date rcvDate, String participant) {
        return sysEvent(rcvDate, frm.memberGranted(rcvDate, participant));
    }

    public String memberRevoked(Date rcvDate, String participant) {
        return sysEvent(rcvDate, frm.memberRevoked(rcvDate, participant));
    }

    public String ownerGranted(Date rcvDate, String participant) {
        return sysEvent(rcvDate, frm.ownerGranted(rcvDate, participant));
    }

    public String ownerRevoked(Date rcvDate, String participant) {
        return sysEvent(rcvDate, frm.ownerRevoked(rcvDate, participant));
    }

    public String adminGranted(Date rcvDate, String participant) {
        return sysEvent(rcvDate, frm.adminGranted(rcvDate, participant));
    }

    public String adminRevoked(Date rcvDate, String participant) {
        return sysEvent(rcvDate, frm.adminRevoked(rcvDate, participant));
    }

    public String moderGranted(Date rcvDate, String participant) {
        return sysEvent(rcvDate, frm.moderGranted(rcvDate, participant));
    }

    public String moderRevoked(Date rcvDate, String participant) {
        return sysEvent(rcvDate, frm.moderRevoked(rcvDate, participant));
    }
}
