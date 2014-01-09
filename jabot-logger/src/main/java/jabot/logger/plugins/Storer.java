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

    @Override
    public LogEntry message(Date rcvDate, String from, String body, boolean fromMe) {
        LogEntry e = createLogEntryWithText(rcvDate, from, body);
        e.setFromMe(fromMe);
        e.setEntryType(EntryType.MSG);

        return e;
    }

    @Override
    public LogEntry subjectMessageOnStart(Date rcvDate, String from, String body) {
        LogEntry e = new LogEntry();

        Addr3D addr = Addr3D.fromRaw(from);
        e.setConference(addr.getNameServer());
        e.setEventDate(Helper.safeDate(rcvDate));
        e.setFrom("");
        e.setText(body);
        e.setEntryType(EntryType.SUBJECTONSTART);

        return e;
    }

    @Override
    public LogEntry delayedMessage(Date rcvDate, String from, String body, boolean fromMe, Date timestamp) {
        LogEntry e = createLogEntryWithText(rcvDate, from, body);
        e.setFromMe(fromMe);
        e.setDelayDate(Helper.safeDate(timestamp));
        e.setEntryType(EntryType.DELAYMSG);

        return e;
    }

    @Override
    public LogEntry setSubject(Date rcvDate, String from, String subject) {
        LogEntry e = createLogEntryWithText(rcvDate, from, subject);
        e.setEntryType(EntryType.SUBJECTSET);

        return e;
    }

    private LogEntry createLogEntryWithText(Date rcvDate, String from, String subject) {
        LogEntry e = new LogEntry();

        Addr3D addr = Addr3D.fromRaw(from);

        e.setConference(addr.getNameServer());
        e.setEventDate(Helper.safeDate(rcvDate));
        e.setFrom(addr.getResource());
        e.setText(subject);
        return e;
    }

    @Override
    public LogEntry kicked(Date rcvDate, String participant, String actor, String reason) {
        LogEntry e = createLogEntryWithText(rcvDate, participant, reason);
        e.setEntryType(EntryType.KICKED);

        return e;
    }

    @Override
    public LogEntry banned(Date rcvDate, String participant, String actor, String reason) {
        LogEntry e = createLogEntryWithText(rcvDate, participant, reason);
        e.setEntryType(EntryType.BANNED);

        return e;
    }

    @Override
    public LogEntry nickChanged(Date rcvDate, String oldNick, String newNick) {
        LogEntry e = createLogEntryWithText(rcvDate, oldNick, newNick);
        e.setEntryType(EntryType.NICKCHANGED);

        return e;
    }

    private LogEntry participant(Date rcvDate, String participant, EntryType entryType) {
        LogEntry e = createLogEntryWithText(rcvDate, participant, "");
        e.setEntryType(entryType);

        return e;
    }

    @Override
    public LogEntry joined(Date rcvDate, String participant) {
        return participant(rcvDate, participant, EntryType.JOINED);
    }

    @Override
    public LogEntry left(Date rcvDate, String participant) {
        return participant(rcvDate, participant, EntryType.LEFT);
    }

    @Override
    public LogEntry voiceGranted(Date rcvDate, String participant) {
        return participant(rcvDate, participant, EntryType.VOICE_GRANTED);
    }

    @Override
    public LogEntry voiceRevoked(Date rcvDate, String participant) {
        return participant(rcvDate, participant, EntryType.VOICE_REVOKED);
    }

    @Override
    public LogEntry memberGranted(Date rcvDate, String participant) {
        return participant(rcvDate, participant, EntryType.MEMBER_GRANTED);
    }

    @Override
    public LogEntry memberRevoked(Date rcvDate, String participant) {
        return participant(rcvDate, participant, EntryType.MEMBER_REVOKED);
    }

    @Override
    public LogEntry moderGranted(Date rcvDate, String participant) {
        return participant(rcvDate, participant, EntryType.MODER_GRANTED);
    }

    @Override
    public LogEntry moderRevoked(Date rcvDate, String participant) {
        return participant(rcvDate, participant, EntryType.MODER_REVOKED);
    }

    @Override
    public LogEntry ownerGranted(Date rcvDate, String participant) {
        return participant(rcvDate, participant, EntryType.OWNER_GRANTED);
    }

    @Override
    public LogEntry ownerRevoked(Date rcvDate, String participant) {
        return participant(rcvDate, participant, EntryType.OWNER_REVOKED);
    }

    @Override
    public LogEntry adminGranted(Date rcvDate, String participant) {
        return participant(rcvDate, participant, EntryType.ADMIN_GRANTED);
    }

    @Override
    public LogEntry adminRevoked(Date rcvDate, String participant) {
        return participant(rcvDate, participant, EntryType.ADMIN_REVOKED);
    }
}
