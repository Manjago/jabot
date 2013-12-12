package jabot;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Properties;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public final class Helper {
    private Helper() {
    }

    public static boolean isEmptyStr(String s) {
        return s == null || s.length() == 0;
    }

    public static boolean isNonEmptyStr(String s) {
        return !isEmptyStr(s);
    }

    public static Properties getProperties(String config) throws JabotException{
        File fileConfig = new File(config);

        if (!fileConfig.exists() || !fileConfig.canRead()) {
            throw  new JabotException(MessageFormat.format("problem with config file {0}", config));
        }

        Properties props = new Properties();
        FileInputStream inStream = null;
        try {
            inStream = new FileInputStream(config);
            props.load(inStream);
        } catch (IOException e) {
            throw new JabotException(MessageFormat.format("fail read file {0}", config), e);
        }
        finally {
            if (inStream != null){
                try {
                    inStream.close();
                } catch (IOException e) {
                    throw new JabotException(MessageFormat.format("fail close stream {0}", config), e);
                }
            }
        }
        return props;
    }


}
