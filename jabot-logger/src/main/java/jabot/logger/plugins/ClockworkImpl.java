package jabot.logger.plugins;

import java.util.Date;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class ClockworkImpl implements Clockwork {

    @Override
    public Date getCurrent() {
        return new Date();
    }
}
