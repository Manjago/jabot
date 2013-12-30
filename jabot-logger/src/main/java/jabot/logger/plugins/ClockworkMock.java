package jabot.logger.plugins;

import jabot.Helper;

import java.util.Date;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class ClockworkMock implements Clockwork {

    private final Date current;

    public ClockworkMock(Date current) {
        this.current = current;
    }

    @Override
    public Date getCurrent() {
        return Helper.safeDate(current);
    }
}
