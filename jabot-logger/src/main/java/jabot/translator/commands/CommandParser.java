package jabot.translator.commands;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public interface CommandParser {
    OperatorCmd parse(String text);
}
