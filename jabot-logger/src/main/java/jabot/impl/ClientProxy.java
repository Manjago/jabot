package jabot.impl;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcCommonsTransportFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public final class ClientProxy {

    private static final int INTMILLISEC_IN_SEC = 1000;
    private static final int HALF_MINUTE = 30;

    private ClientProxy() {
    }

    static XmlRpcClient getXmlRpcClient(Properties properties) throws MalformedURLException {

        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        config.setServerURL(new URL(properties.getProperty("xmlrpc.url", "http://127.0.0.1:8080/xmlrpc")));
        config.setBasicUserName(properties.getProperty("xmlrpc.login", "admin"));
        config.setBasicPassword(properties.getProperty("xmlrpc.password", "password"));
        config.setEnabledForExtensions(false);
        config.setContentLengthOptional(false);
        config.setConnectionTimeout(HALF_MINUTE * INTMILLISEC_IN_SEC);
        config.setReplyTimeout(HALF_MINUTE * INTMILLISEC_IN_SEC);

        XmlRpcClient client = new XmlRpcClient();
        client.setTransportFactory(new XmlRpcCommonsTransportFactory(client));
        client.setConfig(config);
        return client;
    }

}
