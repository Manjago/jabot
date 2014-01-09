package jabot.logger.plugins;

import jabot.logger.LameFunction;
import jabot.logger.dto.EntryType;
import jabot.logger.dto.LogEntry;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class DisplayLogEntry {

    private final Map<EntryType, LameFunction<LogEntry, String>> s;

    public DisplayLogEntry() {
        s = new HashMap<>();

        s.put(EntryType.MSG, new LogEntryFormatter() {
            @Override
            public String execute(LogEntry arg) {
                return message(arg.getEventDate(), arg.getFrom(), arg.getText(), false);
            }
        });
        s.put(EntryType.DELAYMSG, new LogEntryFormatter() {
            @Override
            public String execute(LogEntry arg) {
                return "";
            }
        });
        s.put(EntryType.SUBJECTONSTART, new LogEntryFormatter() {
            @Override
            public String execute(LogEntry arg) {
                return subjectMessageOnStart(arg.getEventDate(), arg.getFrom(), arg.getText());
            }
        });
        s.put(EntryType.SUBJECTSET, new LogEntryFormatter() {
            @Override
            public String execute(LogEntry arg) {
                return setSubject(arg.getEventDate(), arg.getFrom(), arg.getText());
            }
        });
        s.put(EntryType.KICKED, new LogEntryFormatter() {
            @Override
            public String execute(LogEntry arg) {
                return kicked(arg.getEventDate(), arg.getFrom(), "", arg.getText());
            }
        });
        s.put(EntryType.BANNED, new LogEntryFormatter() {
            @Override
            public String execute(LogEntry arg) {
                return banned(arg.getEventDate(), arg.getFrom(), "", arg.getText());
            }
        });
        s.put(EntryType.NICKCHANGED, new LogEntryFormatter() {
            @Override
            public String execute(LogEntry arg) {
                return nickChanged(arg.getEventDate(), arg.getFrom(), arg.getText());
            }
        });
        s.put(EntryType.JOINED, new LogEntryFormatter() {
            @Override
            public String execute(LogEntry arg) {
                return joined(arg.getEventDate(), arg.getFrom());
            }
        });
        s.put(EntryType.LEFT, new LogEntryFormatter() {
            @Override
            public String execute(LogEntry arg) {
                return left(arg.getEventDate(), arg.getFrom());
            }
        });
        s.put(EntryType.VOICE_GRANTED, new LogEntryFormatter() {
            @Override
            public String execute(LogEntry arg) {
                return voiceGranted(arg.getEventDate(), arg.getFrom());
            }
        });
        s.put(EntryType.VOICE_REVOKED, new LogEntryFormatter() {
            @Override
            public String execute(LogEntry arg) {
                return voiceRevoked(arg.getEventDate(), arg.getFrom());
            }
        });
        s.put(EntryType.MEMBER_GRANTED, new LogEntryFormatter() {
            @Override
            public String execute(LogEntry arg) {
                return memberGranted(arg.getEventDate(), arg.getFrom());
            }
        });
        s.put(EntryType.MEMBER_REVOKED, new LogEntryFormatter() {
            @Override
            public String execute(LogEntry arg) {
                return memberRevoked(arg.getEventDate(), arg.getFrom());
            }
        });
        s.put(EntryType.OWNER_GRANTED, new LogEntryFormatter() {
            @Override
            public String execute(LogEntry arg) {
                return ownerGranted(arg.getEventDate(), arg.getFrom());
            }
        });
        s.put(EntryType.OWNER_REVOKED, new LogEntryFormatter() {
            @Override
            public String execute(LogEntry arg) {
                return ownerRevoked(arg.getEventDate(), arg.getFrom());
            }
        });
        s.put(EntryType.ADMIN_GRANTED, new LogEntryFormatter() {
            @Override
            public String execute(LogEntry arg) {
                return adminGranted(arg.getEventDate(), arg.getFrom());
            }
        });
        s.put(EntryType.ADMIN_REVOKED, new LogEntryFormatter() {
            @Override
            public String execute(LogEntry arg) {
                return adminRevoked(arg.getEventDate(), arg.getFrom());
            }
        });
        s.put(EntryType.MODER_GRANTED, new LogEntryFormatter() {
            @Override
            public String execute(LogEntry arg) {
                return moderGranted(arg.getEventDate(), arg.getFrom());
            }
        });
        s.put(EntryType.MODER_REVOKED, new LogEntryFormatter() {
            @Override
            public String execute(LogEntry arg) {
                return moderRevoked(arg.getEventDate(), arg.getFrom());
            }
        });

    }

    String display(LogEntry logEntry) {
        if (logEntry != null
                && logEntry.getEntryType() != null
                && s.containsKey(logEntry.getEntryType())) {
            return s.get(logEntry.getEntryType()).execute(logEntry);
        }
        return null;
    }

}
