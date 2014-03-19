package jabot.logger.plugins;

import jabot.*;
import jabot.chat.ChatInQueueItem;
import jabot.chat.ChatMessage;
import jabot.chat.ChatMessageType;
import jabot.chat.ChatOutQueueItem;
import jabot.db.Database;
import jabot.db.DatabaseFactory;
import jabot.impl.EchomailToolsProxy;
import jabot.logger.LoggerDAO;
import jabot.logger.LoggerDAOImpl;
import jabot.logger.LoggerDatabaseFactoryImpl;
import jabot.logger.dto.LogEntry;
import jabot.room.ConfigurableRoomChatPlugin;
import jabot.room.RoomInQueueItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class RoomLogger extends ConfigurableRoomChatPlugin {

    private static final long MILLISEC_IN_HOUR = 3600000L;
    private static final long MILLISEC_IN_DAY = 86400000L;
    private static final long HOURS_IN_DAY = 24L;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Storer storer;
    private final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
    private Database db;
    private LoggerDAO loggerDao;
    private EchomailToolsProxy echomailToolsProxy;
    private volatile String operator;
    private volatile String area;

    public RoomLogger(String config) throws JabotException {
        super(config);
        scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
        storer = new Storer();
        logger.debug("inited");
    }

    private static Date getNextLaunchDate() {
        Calendar calendar = Calendar.getInstance(Locale.US);
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 1);
        calendar.set(Calendar.SECOND, 0);
        return new Date(calendar.getTime().getTime() + MILLISEC_IN_HOUR * HOURS_IN_DAY);
    }

    @Override
    public void setExecutorProvider(ExecutorProvider executorProvider) {
        super.setExecutorProvider(executorProvider);
        executorProvider.registerSched(scheduledThreadPoolExecutor);
    }

    protected boolean init(Properties props) {

        final Logger logg = LoggerFactory.getLogger(getClass());

        operator = props.getProperty("operator");
        if (operator == null) {
            logg.error("operator not found");
            return false;
        }

        area = props.getProperty("area");
        if (area == null) {
            logg.error("area not found");
            return false;
        }

        DatabaseFactory dbF = new LoggerDatabaseFactoryImpl(props.getProperty("connection"), props.getProperty("user"), props.getProperty("pwd"));
        try {
            db = dbF.create();
            loggerDao = new LoggerDAOImpl(db);
            echomailToolsProxy = new EchomailToolsProxy(props);
        } catch (SQLException e) {
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
                    String psto = getPsto();
                    if (Helper.isNonEmptyStr(psto)) {
                        sendStat(area, psto);
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

    private String getPsto() throws SQLException {
        StatPoster poster = new StatPoster(db);
        final long nowTime = new Date().getTime();
        return poster.report(new Date(nowTime - RoomLogger.MILLISEC_IN_DAY), new Date(nowTime + RoomLogger.MILLISEC_IN_DAY));
    }

    @Override
    public void start() throws InterruptedException {
        if (!isInited() || getRoomOutQueue() == null || getExecutor() == null) {
            logger.error("not inited!");
            return;
        }


        scheduleStatPsto();

        getExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    processCommands();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    logger.error("Chat thread error", e);
                }
            }
        });

        logger.debug("inited, before room process loop");

        while (!Thread.interrupted()) {
            logger.debug("waiting room item");
            RoomInQueueItem item = getRoomInQueue().take();

            logger.debug("got room item {}", item);
            loggi(item);
        }

        logger.info("leave thread");

    }

    private void processCommands() throws InterruptedException {
        while (!Thread.interrupted()) {
            logger.trace("wait chat command");
            ChatInQueueItem item = getChatInQueue().take();

            if (ChatMessageType.MSG.equals(item.getType())) {
                logger.debug("got message item {}", item);

                ChatMessage chatMessage = (ChatMessage) item;

                String simpleAddr;
                try {
                    simpleAddr = Addr3D.fromRaw(chatMessage.getFrom()).getNameServer();
                } catch (IllegalArgumentException e) {
                    logger.error("fail process message {}", chatMessage, e);
                    return;
                }

                if (operator.equals(simpleAddr)) {
                    processOperatorCommand(chatMessage.getBody());
                } else {
                    logger.debug("skip message from wrong address {}", simpleAddr);
                }


            }


        }
    }

    private void chatOut(String s) throws InterruptedException {
        if (Helper.isEmptyStr(s)) {
            return;
        }
        final ChatOutQueueItem chatOutQueueItem = new ChatOutQueueItem(operator, s);
        getChatOutQueue().put(chatOutQueueItem);
        logger.debug("send to chat {}", chatOutQueueItem);
    }

    private void processOperatorCommand(String cmd) throws InterruptedException {
        if (cmd == null) {
            return;
        }

        try {
            switch (cmd) {
                case "POSTME": {
                    chatOut(MessageFormat.format("got cmd {0}", cmd));
                    String psto = getPsto();
                    chatOut(psto);
                    chatOut(MessageFormat.format("process cmd {0}", cmd));
                }
                break;
                case "POST": {
                    chatOut(MessageFormat.format("got cmd {0}", cmd));
                    String psto = getPsto();
                    sendStat("828.test", psto);
                    logger.debug("posted by test request");
                    chatOut(MessageFormat.format("process cmd {0}", cmd));
                }
                break;
                case "REPOST": {
                    chatOut(MessageFormat.format("got cmd {0}", cmd));
                    String psto = getPsto();
                    sendStat(area, psto);
                    logger.debug("posted by request");
                    chatOut(MessageFormat.format("process cmd {0}", cmd));
                }
                break;
                default:
                    chatOut(MessageFormat.format("unknown command cmd {0}", cmd));
                    break;

            }
        } catch (SQLException e) {
            logger.debug("fail process cmd {}", cmd, e);
            chatOut(MessageFormat.format("fail cmd {0}", cmd));
        }

    }

    private void sendStat(String area, String psto) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        echomailToolsProxy.writeEchomail(area, MessageFormat.format("Лог за {0}", dateFormat.format(new Date(new Date().getTime() - MILLISEC_IN_DAY))), psto, "Jabber bot", "All");
    }

    private void loggi(RoomInQueueItem item) {
        try {
            final LogEntry logEntry = (LogEntry) item.display(storer);
            logger.debug("store to db {}", logEntry);
            loggerDao.store(logEntry);
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
                return 1;
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
