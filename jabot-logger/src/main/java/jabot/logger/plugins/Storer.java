package jabot.logger.plugins;

import jabot.Addr3D;
import jabot.Helper;
import jabot.logger.dto.EntryType;
import jabot.logger.dto.LogEntry;
import jabot.room.RoomMessageFormatter;

import java.util.Date;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class Storer implements RoomMessageFormatter {

    private final Clockwork clockwork;

    public Storer(Clockwork clockwork) {
        this.clockwork = clockwork;
    }

    @Override
    public LogEntry message(String from, String body, boolean fromMe) {
        LogEntry e = new LogEntry();

        Addr3D addr = Addr3D.fromRaw(from);

        e.setConference(addr.getNameServer());
        e.setEventDate(Helper.safeDate(clockwork.getCurrent()));
        e.setFrom(addr.getResource());
        e.setText(body);
        e.setFromMe(fromMe);
        e.setEntryType(EntryType.MSG);

        return e;
    }

    @Override
    public LogEntry subjectMessageOnStart(String from, String body) {
        LogEntry e = new LogEntry();

        Addr3D addr = Addr3D.fromRaw(from);
        e.setConference(addr.getNameServer());
        e.setEventDate(Helper.safeDate(clockwork.getCurrent()));
        e.setFrom("");
        e.setText(body);
        e.setEntryType(EntryType.SUBJECTONSTART);

        return e;
    }

    @Override
    public LogEntry delayedMessage(String from, String body, boolean fromMe, Date timestamp) {
        LogEntry e = new LogEntry();

        Addr3D addr = Addr3D.fromRaw(from);

        e.setConference(addr.getNameServer());
        e.setEventDate(Helper.safeDate(clockwork.getCurrent()));
        e.setFrom(addr.getResource());
        e.setText(body);
        e.setFromMe(fromMe);
        e.setDelayDate(Helper.safeDate(timestamp));
        e.setEntryType(EntryType.DELAYMSG);

        return e;
    }

    @Override
    public Object setSubject(String from, String subject) {
        return null;
    }

    @Override
    public Object kicked(String participant, String actor, String reason) {
        return null;
    }

    @Override
    public Object banned(String participant, String actor, String reason) {
        return null;
    }

    @Override
    public Object nickChanged(String oldNick, String newNick) {
        return null;
    }

    @Override
    public Object joined(String participant) {
        return null;
    }

    @Override
    public Object left(String participant) {
        return null;
    }

    @Override
    public Object voiceGranted(String participant) {
        return null;
    }

    @Override
    public Object voiceRevoked(String participant) {
        return null;
    }

    @Override
    public Object memberGranted(String participant) {
        return null;
    }

    @Override
    public Object memberRevoked(String participant) {
        return null;
    }

    @Override
    public Object moderGranted(String participant) {
        return null;
    }

    @Override
    public Object moderRevoked(String participant) {
        return null;
    }

    @Override
    public Object ownerGranted(String participant) {
        return null;
    }

    @Override
    public Object ownerRevoked(String participant) {
        return null;
    }

    @Override
    public Object adminGranted(String participant) {
        return null;
    }

    @Override
    public Object adminRevoked(String participant) {
        return null;
    }
}
