package jabot;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public abstract class PluginVersion {
    public abstract int getMajor();

    public abstract int getMinor();

    @Override
    public String toString() {
        return String.format("v.%d.%d", getMajor(), getMinor());
    }

}
