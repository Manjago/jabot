package jabot.logger;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public interface LameFunction<T, R> {
    R execute(T arg);
}
