package jabot.logger.plugins;

import jabot.ExecutorProvider;
import jabot.Helper;
import jabot.JabotException;
import jabot.PluginVersion;
import jabot.impl.EchomailToolsProxy;
import jabot.logger.DAO;
import jabot.logger.DAOImpl;
import jabot.logger.Database;
import jabot.logger.dto.LogEntry;
import jabot.room.ConfigurableRoomPlugin;
import jabot.room.RoomInQueueItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class RoomLogger extends ConfigurableRoomPlugin {

    private static final long MILLISEC_IN_HOUR = 3600000L;
    private static final int INT = 86400000;
    private static final long HOURS_IN_DAY = 24L;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Storer storer;
    private Database db;
    private DAO dao;
    private EchomailToolsProxy echomailToolsProxy;
    private final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

    public RoomLogger(String config) throws JabotException {
        super(config);
        scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
        storer = new Storer();
        logger.debug("inited");
    }

    @Override
    public void setExecutorProvider(ExecutorProvider executorProvider) {
        super.setExecutorProvider(executorProvider);
        executorProvider.registerSched(scheduledThreadPoolExecutor);
    }

    private static Date getNextLaunchDate() {
        Calendar calendar = Calendar.getInstance(Locale.US);
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 1);
        calendar.set(Calendar.SECOND, 0);
        return new Date(calendar.getTime().getTime() + MILLISEC_IN_HOUR * HOURS_IN_DAY);
    }

    protected boolean init(Properties props) {
        db = Database.init(props.getProperty("connection"), props.getProperty("user"), props.getProperty("pwd"));
        try {
            db.check();
            dao = new DAOImpl(db);
            echomailToolsProxy = new EchomailToolsProxy(props);
        } catch (SQLException e) {
            final Logger logg = LoggerFactory.getLogger(getClass());
            logg.error("fail check database", e);
            return false;
        }
        return true;
    }

    private void scheduleStatPsto() {
        Date showDate = getNextLaunchDate();
        Date now = new Date();
        long initialDelay = showDate.getTime() - now.getTime();
        if (initialDelay < 0) {
            initialDelay = 0;
        }

        logger.info("First stat psto will run at " + showDate
                + " and every 1 day after");
        scheduledThreadPoolExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    StatPoster poster = new StatPoster(db);
                    String psto = poster.report(new Date(new Date().getTime() - INT), new Date(new Date().getTime() + INT));
                    if (Helper.isNonEmptyStr(psto)) {
                        echomailToolsProxy.writeEchomail("828.jabber", "Мегастатистика", psto);
                        logger.debug("posted");
                    } else {
                        logger.debug("empty");
                    }

                } catch (SQLException e) {
                    logger.error("fail post", e);
                }

            }
        },
                initialDelay, MILLISEC_IN_HOUR * HOURS_IN_DAY, TimeUnit.MILLISECONDS);

    }

    @Override
    public void start() throws InterruptedException {
        if (!isInited() || getRoomOutQueue() == null || getExecutor() == null) {
            logger.error("not inited!");
            return;
        }


        scheduleStatPsto();

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
