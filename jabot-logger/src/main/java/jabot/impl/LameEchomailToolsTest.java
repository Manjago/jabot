package jabot.impl;

import org.apache.xmlrpc.XmlRpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Properties;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public final class LameEchomailToolsTest {

    private static final String TEST_JABOT_LOGGER_PROPERTIES = "/opt/jabot/logger.properties";
    private static final Logger LOGGER = LoggerFactory.getLogger(LameEchomailToolsTest.class);

    private LameEchomailToolsTest() {
    }

    public static void main(String[] args) throws XmlRpcException, MalformedURLException {
        Properties props = new Properties();
        tryLoadProperties(props);
        EchomailToolsProxy echomailToolsProxy = new EchomailToolsProxy(props);
        final String result = echomailToolsProxy.writeEchomail("828.test", "Превед", "Всем превед от xml-rpc снова. Здорово!");
        LOGGER.debug(result);
    }

    private static void tryLoadProperties(Properties properties) {
        File config = new File(TEST_JABOT_LOGGER_PROPERTIES);
        if (config.exists() && config.canRead()) {

            try {
                try (final FileInputStream inStream = new FileInputStream(config)) {
                    properties.load(inStream);
                }
            } catch (IOException e) {
                LOGGER.warn("fail load properties", e);
            }
        }
    }

}
