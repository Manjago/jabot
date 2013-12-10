package jabot;

import java.util.concurrent.BlockingQueue;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public interface BotPlugin<O, I> {
    void setOutQueue(BlockingQueue<O> queue);

    void putItem(I item) throws InterruptedException;

    void start() throws InterruptedException;
}
