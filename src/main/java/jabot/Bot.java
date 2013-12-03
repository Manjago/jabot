package jabot;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class Bot {

    private static final int DELAY = 1000;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final BotConfig botConfig;

    public Bot(BotConfig botConfig) {
        if (botConfig == null){
            throw new IllegalArgumentException("bot parameters is null");
        }
        this.botConfig =  new BotConfig(botConfig);
    }

    public void start() throws XMPPException {
        ConnectionConfiguration connConfig = new ConnectionConfiguration(botConfig.getHost(), botConfig.getPort(),
                botConfig.getServiceName());

        logger.debug("botConfig {}", botConfig);

        XMPPConnection connection;

        connection = new XMPPConnection(connConfig);

        connection.connect();
        logger.info("connect ok");

        connection.login(botConfig.getLogin(), botConfig.getPassword());
        logger.info("login ok");

        Roster roster = connection.getRoster();
        roster.setSubscriptionMode(Roster.SubscriptionMode.accept_all);

        //noinspection InfiniteLoopStatement
        while (true) {
            try {
                Thread.sleep(DELAY);
            } catch (InterruptedException e) {
                logger.warn("interrupted", e);
            }
        }

    }


}
