package jabot.logger.plugins;

import jabot.JabotException;
import jabot.PluginVersion;
import jabot.logger.Database;
import jabot.room.ConfigurableRoomPlugin;
import jabot.room.RoomInQueueItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class RoomLogger extends ConfigurableRoomPlugin {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private Database db;

    public RoomLogger(String config) throws JabotException {
        super(config);
        logger.debug("inited");
    }

    protected boolean init(Properties props) {
        db = Database.init(props.getProperty("connection"), props.getProperty("user"), props.getProperty("pwd"));
        return true;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (db != null){
            db.close();
            db = null;
        }
    }

    @Override
    public void start() throws InterruptedException {
        if (!isInited() || getRoomOutQueue() == null || getExecutor() == null) {
            logger.error("not inited!");
            return;
        }

        logger.debug("inited, before room process loop");

        while (!Thread.interrupted()) {
            logger.debug("waiting room item");
            RoomInQueueItem item = getRoomInQueue().take();

            logger.debug("got room item {}", item);
            loggi(item);
        }

        logger.info("leave thread");

    }

    private void loggi(RoomInQueueItem item) {

    }

    @Override
    public PluginVersion getPluginVersion() {
        return new PluginVersion() {
            @Override
            public int getMajor() {
                return 1;
            }

            @Override
            public int getMinor() {
                return 0;
            }
        };
    }
}
