package jabot;

import java.util.List;
import java.util.concurrent.*;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class ExecutorProvider {

    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final List<ScheduledThreadPoolExecutor> sched = new CopyOnWriteArrayList<>();

    public Executor getExecutor() {
        return executor;
    }

    public void registerSched(ScheduledThreadPoolExecutor scheduledThreadPoolExecutor) {
        sched.add(scheduledThreadPoolExecutor);
    }

    public void shutdownNow() {
        for (ScheduledThreadPoolExecutor s : sched) {
            s.shutdownNow();
        }
        executor.shutdownNow();
    }
}
