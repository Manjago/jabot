package jabot;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public interface BotPlugin {
    void start() throws InterruptedException;

    void setExecutorProvider(ExecutorProvider executor);

    PluginVersion getPluginVersion();
}
