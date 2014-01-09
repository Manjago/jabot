package jabot.logger.plugins;

import jabot.DefaulRoomMessageFormatter;
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
public class DisplayLogEntry {

    private final Map<EntryType, LameFunction<LogEntry, String>> s;
    private final SimpleDateFormat format = new SimpleDateFormat("[dd.MM.yyyy hh:mm:ss]");

    public DisplayLogEntry() {
        s = new HashMap<>();
        final RoomMessageFormatter frm = new DefaulRoomMessageFormatter();

        s.put(EntryType.MSG, new LogEntryFormatter(frm, format) {
            @Override
            public String execute(LogEntry arg) {
                return format.format(arg.getEventDate()) + " " + frm.message(arg.getDelayDate(), arg.getFrom(), arg.getText(), false);
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
