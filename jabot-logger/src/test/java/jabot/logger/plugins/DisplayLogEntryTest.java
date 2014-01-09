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

    @Test
    public void testDELAYMSG() throws Exception {
        LogEntry e = storer.delayedMessage(current, "fido828@conference.jabber.ru/tihobot", "тестовое сообщение", false, null);

        String res = d.display(e);
        TestCase.assertNotNull("null наше сообщение", res);
        TestCase.assertEquals("", res);
    }

    @Test
    public void testSUBJECTONSTART() throws Exception {
        LogEntry e = storer.subjectMessageOnStart(current, "fido828@conference.jabber.ru", "сообщение на старте");

        String res = d.display(e);
        TestCase.assertNotNull("null наше сообщение", res);
        TestCase.assertEquals("выдаем фигню", "[09.12.2013 08:06:05] *сообщение на старте*", res);
    }

    @Test
    public void testSUBJECTSET() throws Exception {
        LogEntry e = storer.setSubject(current, "fido828@conference.jabber.ru/Temnenkov", "!");

        String res = d.display(e);
        TestCase.assertNotNull("null наше сообщение", res);
        TestCase.assertEquals("выдаем фигню", "[09.12.2013 08:06:05] *Temnenkov установил(а) тему: \"!\"*", res);
    }

    @Test
    public void testKICKED() throws Exception {
        LogEntry e = storer.kicked(current, "fido828@conference.jabber.ru/tihobot", "actorr", "Temnenkov2: 345");

        String res = d.display(e);
        TestCase.assertNotNull("null наше сообщение", res);
        TestCase.assertEquals("выдаем фигню", "[09.12.2013 08:06:05] *tihobot был выкинут. Причина: Temnenkov2: 345*", res);
    }

    @Test
    public void testBANNED() throws Exception {
        LogEntry e = storer.banned(current, "fido828@conference.jabber.ru/tihobot", "actorr", "Temnenkov2: 345");

        String res = d.display(e);
        TestCase.assertNotNull("null наше сообщение", res);
        TestCase.assertEquals("выдаем фигню", "[09.12.2013 08:06:05] *tihobot был забанен. Причина: Temnenkov2: 345*", res);
    }

    @Test
    public void testNICKCHANGED() throws Exception {
        LogEntry e = storer.nickChanged(current, "fido828@conference.jabber.ru/nick1", "nick2");

        String res = d.display(e);
        TestCase.assertNotNull("null наше сообщение", res);
        TestCase.assertEquals("выдаем фигню", "[09.12.2013 08:06:05] *nick1 теперь известен как nick2*", res);
    }

    @Test
    public void testJOINED() throws Exception {
        LogEntry e = storer.joined(current, "fido828@conference.jabber.ru/nick1");

        String res = d.display(e);
        TestCase.assertNotNull("null наше сообщение", res);
        TestCase.assertEquals("выдаем фигню", "[09.12.2013 08:06:05] *К нам явился дорогой nick1*", res);
    }

    @Test
    public void testLEFT() throws Exception {
        LogEntry e = storer.left(current, "fido828@conference.jabber.ru/nick1");

        String res = d.display(e);
        TestCase.assertNotNull("null наше сообщение", res);
        TestCase.assertEquals("выдаем фигню", "[09.12.2013 08:06:05] *nick1 ушел в жестокий внешний мир*", res);
    }

    @Test
    public void testVOICE_GRANTED() throws Exception {
        LogEntry e = storer.voiceGranted(current, "fido828@conference.jabber.ru/nick1");

        String res = d.display(e);
        TestCase.assertNotNull("null наше сообщение", res);
        TestCase.assertEquals("выдаем фигню", "[09.12.2013 08:06:05] *nick1 получил право голоса*", res);
    }

    @Test
    public void testVOICE_REVOKED() throws Exception {
        LogEntry e = storer.voiceRevoked(current, "fido828@conference.jabber.ru/nick1");

        String res = d.display(e);
        TestCase.assertNotNull("null наше сообщение", res);
        TestCase.assertEquals("выдаем фигню", "[09.12.2013 08:06:05] *nick1 лишился права голоса*", res);
    }

    @Test
    public void testMEMBER_GRANTED() throws Exception {
        LogEntry e = storer.memberGranted(current, "fido828@conference.jabber.ru/nick1");

        String res = d.display(e);
        TestCase.assertNotNull("null наше сообщение", res);
        TestCase.assertEquals("выдаем фигню", "[09.12.2013 08:06:05] *nick1 стал полноправным членом*", res);
    }

    @Test
    public void testMEMBER_REVOKED() throws Exception {
        LogEntry e = storer.memberRevoked(current, "fido828@conference.jabber.ru/nick1");

        String res = d.display(e);
        TestCase.assertNotNull("null наше сообщение", res);
        TestCase.assertEquals("выдаем фигню", "[09.12.2013 08:06:05] *nick1 перестал быть полноправным членом, очень прискорбно*", res);
    }

    @Test
    public void testOWNER_GRANTED() throws Exception {
        LogEntry e = storer.ownerGranted(current, "fido828@conference.jabber.ru/nick1");

        String res = d.display(e);
        TestCase.assertNotNull("null наше сообщение", res);
        TestCase.assertEquals("выдаем фигню", "[09.12.2013 08:06:05] *nick1 стал владельцем!*", res);
    }

    @Test
    public void testOWNER_REVOKED() throws Exception {
        LogEntry e = storer.ownerRevoked(current, "fido828@conference.jabber.ru/nick1");

        String res = d.display(e);
        TestCase.assertNotNull("null наше сообщение", res);
        TestCase.assertEquals("выдаем фигню", "[09.12.2013 08:06:05] *nick1 перестал быть владельцем, бедолага*", res);
    }

    @Test
    public void testADMIN_GRANTED() throws Exception {
        LogEntry e = storer.adminGranted(current, "fido828@conference.jabber.ru/nick1");

        String res = d.display(e);
        TestCase.assertNotNull("null наше сообщение", res);
        TestCase.assertEquals("выдаем фигню", "[09.12.2013 08:06:05] *nick1 стал админом*", res);
    }

    @Test
    public void testADMIN_REVOKED() throws Exception {
        LogEntry e = storer.adminRevoked(current, "fido828@conference.jabber.ru/nick1");

        String res = d.display(e);
        TestCase.assertNotNull("null наше сообщение", res);
        TestCase.assertEquals("выдаем фигню", "[09.12.2013 08:06:05] *nick1 перестал быть админом*", res);
    }

    @Test
    public void testMODER_GRANTED() throws Exception {
        LogEntry e = storer.moderGranted(current, "fido828@conference.jabber.ru/nick1");

        String res = d.display(e);
        TestCase.assertNotNull("null наше сообщение", res);
        TestCase.assertEquals("выдаем фигню", "[09.12.2013 08:06:05] *nick1 стал мурдератором*", res);
    }

    @Test
    public void testMODER_REVOKED() throws Exception {
        LogEntry e = storer.moderRevoked(current, "fido828@conference.jabber.ru/nick1");

        String res = d.display(e);
        TestCase.assertNotNull("null наше сообщение", res);
        TestCase.assertEquals("выдаем фигню", "[09.12.2013 08:06:05] *nick1 перестал быть мурдератором*", res);
    }

}
