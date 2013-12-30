package jabot.logger.plugins;

import jabot.Addr3D;
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
        e.setEventDate(clockwork.getCurrent());
        e.setFrom(addr.getResource());
        e.setText(body);
        e.setFromMe(fromMe);

        //todo тип собщения надо отразить

        return e;
    }

    @Override
    public Object subjectMessage(String from, String body, boolean fromMe) {
        return null;
    }

    @Override
    public Object delayedMessage(String from, String body, boolean fromMe, Date timestamp) {
        return null;
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
