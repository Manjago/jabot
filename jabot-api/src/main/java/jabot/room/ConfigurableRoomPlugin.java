package jabot.room;

import jabot.Helper;
import jabot.JabotException;

import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public abstract class ConfigurableRoomPlugin implements RoomPlugin {
    private volatile boolean inited;
    private BlockingQueue<RoomInQueueItem> roomInQueue;
    private BlockingQueue<RoomOutQueueItem> roomOutQueue;
    private Executor executor;

    public ConfigurableRoomPlugin(String config) throws JabotException {
        Properties props;
        props = Helper.getProperties(config);

        inited = init(props);
    }

    protected BlockingQueue<RoomInQueueItem> getRoomInQueue() {
        return roomInQueue;
    }

    @Override
    public void setRoomInQueue(BlockingQueue<RoomInQueueItem> queue) {
        roomInQueue = queue;
    }

    protected BlockingQueue<RoomOutQueueItem> getRoomOutQueue() {
        return roomOutQueue;
    }

    @Override
    public void setRoomOutQueue(BlockingQueue<RoomOutQueueItem> queue) {
        roomOutQueue = queue;
    }

    protected Executor getExecutor() {
        return executor;
    }

    @Override
    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    protected boolean isInited() {
        return inited;
    }

    protected abstract boolean init(Properties props);


}
