package jabot;

import org.jivesoftware.smack.XMPPException;
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

        String config = args[0];
        File fileConfig = new File(config);

        if (!fileConfig.exists() || !fileConfig.canRead()) {
            err(MessageFormat.format("problem with config file {0}", config));
            return;
        }

        Properties props = new Properties();
        try {
            props.load(new FileInputStream(config));
        } catch (IOException e) {
            LOGGER.error("fail read file {}", config, e);
            return;
        }

        BotConfig b = new BotConfig();
        b.setLogin(props.getProperty("login"));
        b.setPassword(props.getProperty("password"));
        b.setPort(Integer.parseInt(props.getProperty("port")));
        b.setHost(props.getProperty("host"));
        b.setServiceName(props.getProperty("service"));

        Bot bot = new Bot(b);
        try {
            bot.start();
        } catch (XMPPException e) {
            LOGGER.error("Bot error", e);
        }

    }

    private static void err(String err) {
        System.out.println(err);
        LOGGER.error(err);
    }


}
