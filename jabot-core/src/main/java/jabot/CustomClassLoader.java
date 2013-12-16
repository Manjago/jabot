package jabot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class CustomClassLoader extends URLClassLoader {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public CustomClassLoader(URL url) {
        super(new URL[]{url}, null);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        try {
            final Class<?> aClass = super.loadClass(name, resolve);
            logger.debug("custom load class {}", name);
            return aClass;
        } catch (ClassNotFoundException e) {
            logger.debug("ordinary load class {}", name);
            return Class.forName(name, resolve, CustomClassLoader.class.getClassLoader());
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        logger.debug("finalize!! {}", this);
    }

}
