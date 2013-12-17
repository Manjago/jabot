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

            try {
                runBot(props);
            } catch (MalformedURLException e) {
                err("bad urls in config", e);
                return;
            }



        }



    }

    private static void runBot(Properties props) throws MalformedURLException {
        final BotConfig botConfig = getBotConfig(props);

        ClassLoader cl = new CustomClassLoader(botConfig.getPluginJars());

        final ExecutorService executor = Executors.newCachedThreadPool();

        final Bot bot = new Bot(botConfig, executor, cl);

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

        try {
            Thread.sleep(20000); //test
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        bot.stop();
        List<Runnable> bads =  executor.shutdownNow();
        LOGGER.info("shutdown, bad count {}", bads.size());

    }

    private static BotConfig getBotConfig(Properties props) throws MalformedURLException {
        BotConfig b = new BotConfig();
        b.setLogin(props.getProperty("login"));
        b.setPassword(props.getProperty("password"));
        b.setPort(Integer.parseInt(props.getProperty("port")));
        b.setHost(props.getProperty("host"));
        b.setServiceName(props.getProperty("service"));
        b.setChatPlugins(props.getProperty("chatPlugins", ""));
        b.setRoomsConfig(props.getProperty("roomsConfig", ""));

        String allJars = props.getProperty("pluginsJars", "");
        if (Helper.isNonEmptyStr(allJars)){
            String[] jars = allJars.split(";");
            URL[] urls = new URL[jars.length];
            for(int i=0; i < jars.length; ++i){
               urls[i] = new URL(jars[i]);
            }
            b.setPluginJars(urls);
        }

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
