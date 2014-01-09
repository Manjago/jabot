package jabot.logger.plugins;

import jabot.JabotException;
import jabot.PluginVersion;
import jabot.logger.DAO;
import jabot.logger.DAOImpl;
import jabot.logger.Database;
import jabot.logger.dto.LogEntry;
import jabot.room.ConfigurableRoomPlugin;
import jabot.room.RoomInQueueItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Properties;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class RoomLogger extends ConfigurableRoomPlugin {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private Database db;
    private final Storer storer;
    private DAO dao;

    public RoomLogger(String config) throws JabotException {
        super(config);
        storer = new Storer();
        logger.debug("inited");
    }

    protected boolean init(Properties props) {
        db = Database.init(props.getProperty("connection"), props.getProperty("user"), props.getProperty("pwd"));
        try {
            db.check();
            dao = new DAOImpl(db);
        } catch (SQLException e) {
            final Logger logg = LoggerFactory.getLogger(getClass());
            logg.error("fail check database", e);
            return false;
        }
        return true;
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
        try {
            final LogEntry logEntry = (LogEntry) item.display(storer);
            logger.debug("store to db {}", logEntry);
            dao.store(logEntry);
        } catch (SQLException | JabotException e) {
            logger.error("fail store message {}", item, e);
        }
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

    @Override
    protected void finalize() throws Throwable {
        try {
            if (db != null) {
                db.close();
            }
        } finally {
            super.finalize();
        }
    }


}
