package jabot.logger.plugins;

import jabot.logger.dto.LogEntry;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class DisplayLogEntryTest {

    private final Date current = new Date(2013 - 1900, 12 - 1, 9, 8, 6, 5);
    private Storer storer = new Storer();
    private DisplayLogEntry d;

    @Before
    public void setUp() throws Exception {
        d = new DisplayLogEntry();
    }

    @Test
    public void testMSG() throws Exception {
        LogEntry e = storer.message(current, "fido828@conference.jabber.ru/tihobot", "тестовое сообщение", false);

        String res = d.display(e);
        TestCase.assertNotNull("null наше сообщение", res);
        TestCase.assertEquals("выдаем фигню", "[09.12.2013 08:06:05] tihobot: тестовое сообщение", res);
    }
}
