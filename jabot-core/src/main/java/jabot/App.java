package jabot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Properties;

public final class App {

    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    private App() {
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            err("need config file name in command string");
            return;
        }

        Properties props = getProperties(args[0]);
        if (props == null) {
            return;
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                LOGGER.info("shutdown");
            }
        });

        final Bot bot = new Bot(getBotConfig(props));

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    bot.start();
                } catch (Exception e) {
                    LOGGER.error("Bot error", e);
                }
            }
        }).start();

        LOGGER.info("started");

    }

    private static Properties getProperties(String config) {
        File fileConfig = new File(config);

        if (!fileConfig.exists() || !fileConfig.canRead()) {
            err(MessageFormat.format("problem with config file {0}", config));
            return null;
        }

        Properties props = new Properties();
        FileInputStream inStream = null;
        try {
            inStream = new FileInputStream(config);
            props.load(inStream);
        } catch (IOException e) {
            LOGGER.error("fail read file {}", config, e);
            return null;
        }
        finally {
            if (inStream != null){
                try {
                    inStream.close();
                } catch (IOException e) {
                    LOGGER.error("fail close stream {}", config, e);
                }
            }
        }
        return props;
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


}
