package jabot.logger.plugins;

import jabot.logger.dto.EntryType;
import jabot.logger.dto.LogEntry;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertNotNull;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class StorerTest {

    private final Date current = new Date(2013, 12, 11, 8, 6, 5);
    private Storer storer;

    @Before
    public void setUp() throws Exception {
        storer = new Storer(new Clockwork() {
            @Override
            public Date getCurrent() {
                return current;
            }
        });
    }

    @Test
    public void testMessageNotFromMe() throws Exception {
        LogEntry e = storer.message("fido828@conference.jabber.ru/tihobot", "тестовое сообщение", false);

        assertNotNull(e);
        assertEquals("fido828@conference.jabber.ru", e.getConference());
        assertEquals("tihobot", e.getFrom());
        assertEquals("тестовое сообщение", e.getText());
        assertEquals(current, e.getEventDate());
        assertEquals(0L, e.getId());
        assertEquals(true, e.isValid());
        assertEquals(false, e.isFromMe());
        assertEquals(EntryType.MSG, e.getEntryType());
    }

    @Test
    public void testMessageFromMe() throws Exception {
        LogEntry e = storer.message("fido828@conference.jabber.ru/tihobot", "тестовое сообщение", true);

        assertNotNull(e);
        assertEquals("fido828@conference.jabber.ru", e.getConference());
        assertEquals("tihobot", e.getFrom());
        assertEquals("тестовое сообщение", e.getText());
        assertEquals(current, e.getEventDate());
        assertEquals(0L, e.getId());
        assertEquals(true, e.isValid());
        assertEquals(true, e.isFromMe());
        assertEquals(EntryType.MSG, e.getEntryType());
    }

    @Test
    public void testSubjectMessage() throws Exception {

    }

    @Test
    public void testDelayedMessage() throws Exception {

    }

    @Test
    public void testSetSubject() throws Exception {

    }

    @Test
    public void testKicked() throws Exception {

    }

    @Test
    public void testBanned() throws Exception {

    }

    @Test
    public void testNickChanged() throws Exception {

    }

    @Test
    public void testJoined() throws Exception {

    }

    @Test
    public void testLeft() throws Exception {

    }

    @Test
    public void testVoiceGranted() throws Exception {

    }

    @Test
    public void testVoiceRevoked() throws Exception {

    }

    @Test
    public void testMemberGranted() throws Exception {

    }

    @Test
    public void testMemberRevoked() throws Exception {

    }

    @Test
    public void testModerGranted() throws Exception {

    }

    @Test
    public void testModerRevoked() throws Exception {

    }

    @Test
    public void testOwnerGranted() throws Exception {

    }

    @Test
    public void testOwnerRevoked() throws Exception {

    }

    @Test
    public void testAdminGranted() throws Exception {

    }

    @Test
    public void testAdminRevoked() throws Exception {

    }
}
