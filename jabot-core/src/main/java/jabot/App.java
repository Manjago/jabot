package jabot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public final class App {

    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);
    private static final int BOUND = 20;
    private static final int SLEEP = 5000;

    private App() {
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            err("need config file name in command string");
            return;
        }

        Properties props = null;
        try {
            props = Helper.getProperties(args[0]);
        } catch (JabotException e) {
            err("fail load properties file", e);
            return;
        }

        if (props == null) {
            err("empty properties file");
            return;
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                LOGGER.info("shutdown");
            }
        });


        try {
            while (!Thread.interrupted()) {
                runBot(props);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }


    }

    private static void runBot(Properties props) throws InterruptedException {
        final BotConfig botConfig = getBotConfig(props);

        final ExecutorProvider executorProvider = new ExecutorProvider();

        final BlockingQueue<Object> ctrlQueue = new LinkedBlockingQueue<>(BOUND);
        final Bot bot = new Bot(botConfig, executorProvider, ctrlQueue);

        executorProvider.getExecutor().execute(new Runnable() {

            @Override
            public void run() {
                try {
                    bot.start();
                } catch (Exception e) {
                    LOGGER.error("Bot error", e);
                }
            }
        });

        ctrlQueue.take();
        LOGGER.info("restart");
        bot.stop();
        executorProvider.shutdownNow();
        Thread.sleep(SLEEP);
    }

    private static BotConfig getBotConfig(Properties props) {
        BotConfig b = new BotConfig();
        b.setLogin(props.getProperty("login"));
        b.setPassword(props.getProperty("password"));
        b.setPort(Integer.parseInt(props.getProperty("port")));
        b.setHost(props.getProperty("host"));
        b.setServiceName(props.getProperty("service"));
        b.setChatPlugins(props.getProperty("chatPlugins", ""));
        b.setRoomsConfig(props.getProperty("roomsConfig", ""));

        return b;
    }

    private static void err(String err) {
        System.out.println(err);
        LOGGER.error(err);
    }

    private static void err(String err, Exception e) {
        System.out.println(err);
        LOGGER.error(err, e);
    }

}
