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
        LogEntry e = createLogEntryWithText(from, body);
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
        LogEntry e = createLogEntryWithText(from, body);
        e.setFromMe(fromMe);
        e.setDelayDate(Helper.safeDate(timestamp));
        e.setEntryType(EntryType.DELAYMSG);

        return e;
    }

    @Override
    public LogEntry setSubject(String from, String subject) {
        LogEntry e = createLogEntryWithText(from, subject);
        e.setEntryType(EntryType.SUBJECTSET);

        return e;
    }

    private LogEntry createLogEntryWithText(String from, String subject) {
        LogEntry e = new LogEntry();

        Addr3D addr = Addr3D.fromRaw(from);

        e.setConference(addr.getNameServer());
        e.setEventDate(Helper.safeDate(clockwork.getCurrent()));
        e.setFrom(addr.getResource());
        e.setText(subject);
        return e;
    }

    @Override
    public LogEntry kicked(String participant, String actor, String reason) {
        LogEntry e = createLogEntryWithText(participant, reason);
        e.setEntryType(EntryType.KICKED);

        return e;
    }

    @Override
    public LogEntry banned(String participant, String actor, String reason) {
        LogEntry e = createLogEntryWithText(participant, reason);
        e.setEntryType(EntryType.BANNED);

        return e;
    }

    @Override
    public LogEntry nickChanged(String oldNick, String newNick) {
        LogEntry e = createLogEntryWithText(oldNick, newNick);
        e.setEntryType(EntryType.NICKCHANGED);

        return e;
    }

    private LogEntry participant(String participant, EntryType entryType) {
        LogEntry e = createLogEntryWithText(participant, "");
        e.setEntryType(entryType);

        return e;
    }

    @Override
    public LogEntry joined(String participant) {
        return participant(participant, EntryType.JOINED);
    }

    @Override
    public LogEntry left(String participant) {
        return participant(participant, EntryType.LEFT);
    }

    @Override
    public LogEntry voiceGranted(String participant) {
        return participant(participant, EntryType.VOICE_GRANTED);
    }

    @Override
    public LogEntry voiceRevoked(String participant) {
        return participant(participant, EntryType.VOICE_REVOKED);
    }

    @Override
    public LogEntry memberGranted(String participant) {
        return participant(participant, EntryType.MEMBER_GRANTED);
    }

    @Override
    public LogEntry memberRevoked(String participant) {
        return participant(participant, EntryType.MEMBER_REVOKED);
    }

    @Override
    public LogEntry moderGranted(String participant) {
        return participant(participant, EntryType.MODER_GRANTED);
    }

    @Override
    public LogEntry moderRevoked(String participant) {
        return participant(participant, EntryType.MODER_REVOKED);
    }

    @Override
    public LogEntry ownerGranted(String participant) {
        return participant(participant, EntryType.OWNER_GRANTED);
    }

    @Override
    public LogEntry ownerRevoked(String participant) {
        return participant(participant, EntryType.OWNER_REVOKED);
    }

    @Override
    public LogEntry adminGranted(String participant) {
        return participant(participant, EntryType.ADMIN_GRANTED);
    }

    @Override
    public LogEntry adminRevoked(String participant) {
        return participant(participant, EntryType.ADMIN_REVOKED);
    }
}
