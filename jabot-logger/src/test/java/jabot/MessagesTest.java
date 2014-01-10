package jabot;

import junit.framework.TestCase;
import org.junit.Test;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class MessagesTest {
    @Test
    public void testFormat() throws Exception {

        Messages m = new Messages();
        TestCase.assertEquals("1 установил(а) тему: \"2\"", m.format(Messages.SET_SUBJECT, 1, 2));
    }

    @Test
    public void testNotFound() throws Exception {

        Messages m = new Messages();
        TestCase.assertEquals("", m.format("setSubject111", 1, 2));
    }

}
