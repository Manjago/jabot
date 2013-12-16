package jabot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class App {

    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

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


        while(!Thread.interrupted()){

            {
                URL url = null;
                try {
                    url = new URL("file:///C:\\temp\\bot\\jabot-translator-1.0-SNAPSHOT.jar");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                ClassLoader cl = new CustomClassLoader(url);
                ExecutorService executor = Executors.newCachedThreadPool();
                final Bot bot = new Bot(getBotConfig(props), executor, cl);

                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            bot.start();
                        } catch (Exception e) {
                            LOGGER.error("Bot error", e);
                        }
                    }
                });

                LOGGER.info("started");
                try {
                    Thread.sleep(20000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                LOGGER.info("try stop");
                bot.stop();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                LOGGER.info("try shutdown");
                List<Runnable> bads =  executor.shutdownNow();

                LOGGER.debug("bad count {}", bads.size() );

            }

            System.gc();
            LOGGER.info("wait after gc ok");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            LOGGER.info("start again hahahaha");

        }



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
