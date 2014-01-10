package jabot.impl;

import org.apache.xmlrpc.XmlRpcException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Properties;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public final class LameEchomailToolsTest {
    private LameEchomailToolsTest() {
    }

    public static void main(String[] args) throws XmlRpcException, MalformedURLException {
        Properties props = new Properties();
        tryLoadProperties(props);
        EchomailToolsProxy echomailToolsProxy = new EchomailToolsProxy(props);
        System.out.println(echomailToolsProxy.writeEchomail("828.test", "Превед", "Всем превед от xml-rpc снова. Здорово!"));
    }

    private static void tryLoadProperties(Properties properties) {
        File config = new File("/opt/jabot/logger.properties");
        if (config.exists() && config.canRead()) {

            try {
                properties.load(new FileInputStream(config));
            } catch (IOException ignored) {
            }
        }
    }

}
