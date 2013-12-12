package jabot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class Loader<E> {

    private final Logger logger = LoggerFactory.getLogger(Loader.class);

    public List<E> loadPlugins(String plugins) {

        if (plugins == null) {
            throw new IllegalArgumentException("plugins");
        }

        List<E> result = new ArrayList<>();
        String[] modules = plugins.split(";");

        for (String module : modules) {
            {
                String className;
                String config;
                int idx = module.indexOf(':');
                if (idx < 0) {
                    logger.trace("Skipping config string " + module);
                    className = module;
                    config = null;
                } else {
                    className = module.substring(0, idx);
                    config = module.substring(idx + 1);
                }
                Class<?> clazz = null;
                try {
                    clazz = Class.forName(className);
                } catch (ClassNotFoundException e) {
                    logger.error("not found plugin {}, skipped", className, e);
                    continue;
                }
                E plugin;
                try {

                    plugin = instPlugin(config, clazz);

                } catch (InstantiationException e) {
                    skipBadPlugin(className, e);
                    continue;
                } catch (IllegalAccessException e) {
                    skipBadPlugin(className, e);
                    continue;
                } catch (InvocationTargetException e) {
                    skipBadPlugin(className, e);
                    continue;
                } catch (NoSuchMethodException e) {
                    skipBadPlugin(className, e);
                    continue;
                }

                logger.info("loaded plugin {}", plugin);
                result.add(plugin);
            }
        }

        return result;
    }

    private E instPlugin(String config, Class<?> clazz) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        if (config == null) {
            return (E) clazz
                    .getConstructor().newInstance();

        } else {
            return (E) clazz
                    .getConstructor(String.class).newInstance(
                            config);
        }
    }

    private void skipBadPlugin(String className, Exception e) {
        logger.error("fail instantiate plugin {}, skipped", className, e);
    }
}
