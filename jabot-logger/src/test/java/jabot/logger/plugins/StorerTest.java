package jabot.logger.plugins;

import jabot.logger.DAO;
import jabot.logger.DAOImpl;
import jabot.logger.Database;
import jabot.logger.dto.EntryType;
import jabot.logger.dto.LogEntry;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.Date;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class StorerTest {

    private final Date current = new Date(2013, 12, 11, 8, 6, 5);
    private Storer storer;

    @Before
    public void setUp() throws Exception {
        storer = new Storer(new ClockworkMock(current));
    }

    private LogEntry storeAndLoad(LogEntry logEntry) throws SQLException {
        try (Database db = Database.init("jdbc:h2:mem:test", "sa", "sa")) {
            db.check();

            DAO dao = new DAOImpl(db);

            dao.store(logEntry);
            return dao.getById(1L);
        }
    }

    @Test
    public void testMessageNotFromMe() throws Exception {
        LogEntry e = storer.message("fido828@conference.jabber.ru/tihobot", "тестовое сообщение", false);

        checkMessageNotFromMe(e);

        checkMessageNotFromMe(storeAndLoad(e));
    }

    private void checkMessageNotFromMe(LogEntry e) {
        assertNotNull(e);
        assertEquals("fido828@conference.jabber.ru", e.getConference());
        assertEquals("tihobot", e.getFrom());
        assertEquals("тестовое сообщение", e.getText());
        assertEquals(current, e.getEventDate());
        assertEquals(true, e.isValid());
        assertEquals(false, e.isFromMe());
        assertEquals(EntryType.MSG, e.getEntryType());
    }

    @Test
    public void testMessageFromMe() throws Exception {
        LogEntry e = storer.message("fido828@conference.jabber.ru/tihobot", "тестовое сообщение", true);

        checkMessageFromMe(e);
        checkMessageFromMe(storeAndLoad(e));
    }

    private void checkMessageFromMe(LogEntry e) {
        assertNotNull(e);
        assertEquals("fido828@conference.jabber.ru", e.getConference());
        assertEquals("tihobot", e.getFrom());
        assertEquals("тестовое сообщение", e.getText());
        assertEquals(current, e.getEventDate());
        assertEquals(true, e.isValid());
        assertEquals(true, e.isFromMe());
        assertEquals(EntryType.MSG, e.getEntryType());
    }

    @Test
    public void testSubjectMessageOnStart() throws Exception {
        LogEntry e = storer.subjectMessageOnStart("fido828@conference.jabber.ru", "сообщение на старте");

        checkSubjectMessageOnStart(e);
        checkSubjectMessageOnStart(storeAndLoad(e));
    }

    private void checkSubjectMessageOnStart(LogEntry e) {
        assertNotNull("все пропало - нулл наше сообщение на старте", e);
        assertEquals("fido828@conference.jabber.ru", e.getConference());
        assertEquals("сообщение на старте", e.getText());
        assertEquals(current, e.getEventDate());
        assertEquals(true, e.isValid());
        assertEquals(EntryType.SUBJECTONSTART, e.getEntryType());
    }

    @Test
    public void testDelayedMessage() throws Exception {
        LogEntry e = storer.delayedMessage("fido828@conference.jabber.ru/tihobot", "тестовое сообщение", true, new Date(2022, 6, 7, 8, 9, 10));

        checkDelayedMessage(e);
        checkDelayedMessage(storeAndLoad(e));
    }

    private void checkDelayedMessage(LogEntry e) {
        assertNotNull("все пропало - нулл наше старое сообщение", e);
        assertEquals("fido828@conference.jabber.ru", e.getConference());
        assertEquals("tihobot", e.getFrom());
        assertEquals("тестовое сообщение", e.getText());
        assertEquals(current, e.getEventDate());
        assertEquals(true, e.isValid());
        assertEquals(true, e.isFromMe());
        assertEquals(new Date(2022, 6, 7, 8, 9, 10), e.getDelayDate());
        assertEquals(EntryType.DELAYMSG, e.getEntryType());
    }

    @Test
    public void testSetSubject() throws Exception {
        LogEntry e = storer.setSubject("fido828@conference.jabber.ru/Temnenkov", "!");

        checkSetSubject(e);
        checkSetSubject(storeAndLoad(e));
    }

    private void checkSetSubject(LogEntry e) {
        assertNotNull("все пропало - нулл наша установка субжекта", e);
        assertEquals("fido828@conference.jabber.ru", e.getConference());
        assertEquals("Temnenkov", e.getFrom());
        assertEquals("!", e.getText());
        assertEquals(current, e.getEventDate());
        assertEquals(true, e.isValid());
        assertEquals(EntryType.SUBJECTSET, e.getEntryType());
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