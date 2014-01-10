package jabot.logger.plugins;

import jabot.DefaultRoomMessageFormatter;
import jabot.Messages;
import jabot.logger.LameFunction;
import jabot.logger.dto.EntryType;
import jabot.logger.dto.LogEntry;
import jabot.room.RoomMessageFormatter;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class LogEntryDisplayer {

    private final Map<EntryType, LameFunction<LogEntry, String>> s;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("[dd.MM.yyyy hh:mm:ss]");
    private final RoomMessageFormatter frm = new DefaultRoomMessageFormatter(new Messages());

    public LogEntryDisplayer() {

        s = new HashMap<>();
        init1();
        init2();
        init3();
    }

    private void init1() {
        s.put(EntryType.MSG, new LogEntryFormatter(dateFormat, frm) {
            @Override
            public String execute(LogEntry arg) {
                return message(arg.getEventDate(), arg.getFrom(), arg.getText(), false);
            }
        });
        s.put(EntryType.DELAYMSG, new LogEntryFormatter(dateFormat, frm) {
            @Override
            public String execute(LogEntry arg) {
                return "";
            }
        });
        s.put(EntryType.SUBJECTONSTART, new LogEntryFormatter(dateFormat, frm) {
            @Override
            public String execute(LogEntry arg) {
                return subjectMessageOnStart(arg.getEventDate(), arg.getFrom(), arg.getText());
            }
        });
        s.put(EntryType.SUBJECTSET, new LogEntryFormatter(dateFormat, frm) {
            @Override
            public String execute(LogEntry arg) {
                return setSubject(arg.getEventDate(), arg.getFrom(), arg.getText());
            }
        });
        s.put(EntryType.KICKED, new LogEntryFormatter(dateFormat, frm) {
            @Override
            public String execute(LogEntry arg) {
                return kicked(arg.getEventDate(), arg.getFrom(), "", arg.getText());
            }
        });
        s.put(EntryType.BANNED, new LogEntryFormatter(dateFormat, frm) {
            @Override
            public String execute(LogEntry arg) {
                return banned(arg.getEventDate(), arg.getFrom(), "", arg.getText());
            }
        });
        s.put(EntryType.NICKCHANGED, new LogEntryFormatter(dateFormat, frm) {
            @Override
            public String execute(LogEntry arg) {
                return nickChanged(arg.getEventDate(), arg.getFrom(), arg.getText());
            }
        });
        s.put(EntryType.JOINED, new LogEntryFormatter(dateFormat, frm) {
            @Override
            public String execute(LogEntry arg) {
                return joined(arg.getEventDate(), arg.getFrom());
            }
        });
        s.put(EntryType.LEFT, new LogEntryFormatter(dateFormat, frm) {
            @Override
            public String execute(LogEntry arg) {
                return left(arg.getEventDate(), arg.getFrom());
            }
        });

    }

    private void init2() {
        s.put(EntryType.VOICE_GRANTED, new LogEntryFormatter(dateFormat, frm) {
            @Override
            public String execute(LogEntry arg) {
                return voiceGranted(arg.getEventDate(), arg.getFrom());
            }
        });
        s.put(EntryType.VOICE_REVOKED, new LogEntryFormatter(dateFormat, frm) {
            @Override
            public String execute(LogEntry arg) {
                return voiceRevoked(arg.getEventDate(), arg.getFrom());
            }
        });
        s.put(EntryType.MEMBER_GRANTED, new LogEntryFormatter(dateFormat, frm) {
            @Override
            public String execute(LogEntry arg) {
                return memberGranted(arg.getEventDate(), arg.getFrom());
            }
        });
        s.put(EntryType.MEMBER_REVOKED, new LogEntryFormatter(dateFormat, frm) {
            @Override
            public String execute(LogEntry arg) {
                return memberRevoked(arg.getEventDate(), arg.getFrom());
            }
        });
        s.put(EntryType.OWNER_GRANTED, new LogEntryFormatter(dateFormat, frm) {
            @Override
            public String execute(LogEntry arg) {
                return ownerGranted(arg.getEventDate(), arg.getFrom());
            }
        });
        s.put(EntryType.OWNER_REVOKED, new LogEntryFormatter(dateFormat, frm) {
            @Override
            public String execute(LogEntry arg) {
                return ownerRevoked(arg.getEventDate(), arg.getFrom());
            }
        });
        s.put(EntryType.ADMIN_GRANTED, new LogEntryFormatter(dateFormat, frm) {
            @Override
            public String execute(LogEntry arg) {
                return adminGranted(arg.getEventDate(), arg.getFrom());
            }
        });
        s.put(EntryType.ADMIN_REVOKED, new LogEntryFormatter(dateFormat, frm) {
            @Override
            public String execute(LogEntry arg) {
                return adminRevoked(arg.getEventDate(), arg.getFrom());
            }
        });
    }

    private void init3() {
        s.put(EntryType.MODER_GRANTED, new LogEntryFormatter(dateFormat, frm) {
            @Override
            public String execute(LogEntry arg) {
                return moderGranted(arg.getEventDate(), arg.getFrom());
            }
        });
        s.put(EntryType.MODER_REVOKED, new LogEntryFormatter(dateFormat, frm) {
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
