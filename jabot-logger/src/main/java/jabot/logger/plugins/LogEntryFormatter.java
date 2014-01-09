package jabot.logger.plugins;

import jabot.logger.LameFunction;
import jabot.logger.dto.LogEntry;
import jabot.room.RoomMessageFormatter;

import java.text.SimpleDateFormat;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public abstract class LogEntryFormatter implements LameFunction<LogEntry, String> {

    private final RoomMessageFormatter f;
    private final SimpleDateFormat dateFormat;

    protected LogEntryFormatter(RoomMessageFormatter f, SimpleDateFormat dateFormat) {
        this.f = f;
        this.dateFormat = dateFormat;
    }

}
