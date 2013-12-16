package jabot;

import java.util.concurrent.Executor;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public interface BotPlugin {
    void start() throws InterruptedException;
    void setExecutor(Executor executor);
}
