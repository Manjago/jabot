package jabot.logger.plugins;

import jabot.db.Database;
import jabot.db.DatabaseFactory;
import jabot.logger.DAO;
import jabot.logger.DAOImpl;
import jabot.logger.LoggerDatabaseFactoryImpl;
import jabot.logger.dto.EntryType;
import jabot.logger.dto.LogEntry;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class StorerTest {

    private final Date current = new Date(2013, 12, 11, 8, 6, 5);
    private Storer storer;
    private DatabaseFactory dbF;

    @Before
    public void setUp() throws Exception {
        dbF = new LoggerDatabaseFactoryImpl("jdbc:h2:mem:test", "sa", "sa");
        storer = new Storer();
    }

    private LogEntry storeAndLoad(LogEntry logEntry) throws Exception {
        try (Database db = dbF.create()) {

            DAO dao = new DAOImpl(db);

            dao.store(logEntry);
            return dao.getById(1L);
        }
    }

    @Test
    public void testMessageNotFromMe() throws Exception {
        LogEntry e = storer.message(current, "fido828@conference.jabber.ru/tihobot", "тестовое сообщение", false);

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
        LogEntry e = storer.message(current, "fido828@conference.jabber.ru/tihobot", "тестовое сообщение", true);

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
        LogEntry e = storer.subjectMessageOnStart(current, "fido828@conference.jabber.ru", "сообщение на старте");

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
        LogEntry e = storer.delayedMessage(current, "fido828@conference.jabber.ru/tihobot", "тестовое сообщение", true, new Date(2022, 6, 7, 8, 9, 10));

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
        LogEntry e = storer.setSubject(current, "fido828@conference.jabber.ru/Temnenkov", "!");

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
        LogEntry e = storer.kicked(current, "fido828@conference.jabber.ru/tihobot", "actorr", "Temnenkov2: 345");

        checkKicked(e);
        checkKicked(storeAndLoad(e));
    }

    private void checkKicked(LogEntry e) {
        assertNotNull("все пропало - нулл наша инфа по кикеду", e);
        assertEquals("fido828@conference.jabber.ru", e.getConference());
        assertEquals("tihobot", e.getFrom());
        assertEquals("Temnenkov2: 345", e.getText());
        assertEquals(current, e.getEventDate());
        assertEquals(true, e.isValid());
        assertEquals(EntryType.KICKED, e.getEntryType());
    }

    @Test
    public void testBanned() throws Exception {
        LogEntry e = storer.banned(current, "fido828@conference.jabber.ru/tihobot", "actorr", "Temnenkov2: 345");

        checkBanned(e);
        checkBanned(storeAndLoad(e));
    }

    private void checkBanned(LogEntry e) {
        assertNotNull("все пропало - нулл наша инфа по баннеду", e);
        assertEquals("fido828@conference.jabber.ru", e.getConference());
        assertEquals("tihobot", e.getFrom());
        assertEquals("Temnenkov2: 345", e.getText());
        assertEquals(current, e.getEventDate());
        assertEquals(true, e.isValid());
        assertEquals(EntryType.BANNED, e.getEntryType());
    }

    @Test
    public void testNickChanged() throws Exception {
        LogEntry e = storer.nickChanged(current, "fido828@conference.jabber.ru/nick1", "nick2");

        checkNickChanged(e);
        checkNickChanged(storeAndLoad(e));
    }

    private void checkNickChanged(LogEntry e) {
        assertNotNull("все пропало - нулл наша инфа смене ника", e);
        assertEquals("fido828@conference.jabber.ru", e.getConference());
        assertEquals("nick1", e.getFrom());
        assertEquals("nick2", e.getText());
        assertEquals(current, e.getEventDate());
        assertEquals(true, e.isValid());
        assertEquals(EntryType.NICKCHANGED, e.getEntryType());
    }

    @Test
    public void testJoined() throws Exception {
        LogEntry e = storer.joined(current, "fido828@conference.jabber.ru/nick1");

        testParticipant(e, "joined", EntryType.JOINED);
    }

    @Test
    public void testLeft() throws Exception {
        LogEntry e = storer.left(current, "fido828@conference.jabber.ru/nick1");

        testParticipant(e, "left", EntryType.LEFT);
    }

    private void testParticipant(LogEntry e, String textNull, EntryType entryType) throws Exception {
        checkParticipant(e, textNull, entryType);
        checkParticipant(storeAndLoad(e), textNull, entryType);
    }

    private void checkParticipant(LogEntry e, String textNull, EntryType entryType) {
        assertNotNull("все пропало - нулл наша инфа про " + textNull, e);
        assertEquals("fido828@conference.jabber.ru", e.getConference());
        assertEquals("nick1", e.getFrom());
        assertEquals(current, e.getEventDate());
        assertEquals(true, e.isValid());
        assertEquals(entryType, e.getEntryType());
    }

    @Test
    public void testVoiceGranted() throws Exception {
        LogEntry e = storer.voiceGranted(current, "fido828@conference.jabber.ru/nick1");

        testParticipant(e, "voiceGranted", EntryType.VOICE_GRANTED);
    }

    @Test
    public void testVoiceRevoked() throws Exception {
        LogEntry e = storer.voiceRevoked(current, "fido828@conference.jabber.ru/nick1");

        testParticipant(e, "voiceRevoked", EntryType.VOICE_REVOKED);

    }

    @Test
    public void testMemberGranted() throws Exception {
        LogEntry e = storer.memberGranted(current, "fido828@conference.jabber.ru/nick1");

        testParticipant(e, "memberGranted", EntryType.MEMBER_GRANTED);
    }

    @Test
    public void testMemberRevoked() throws Exception {
        LogEntry e = storer.memberRevoked(current, "fido828@conference.jabber.ru/nick1");

        testParticipant(e, "memberRevoked", EntryType.MEMBER_REVOKED);
    }

    @Test
    public void testModerGranted() throws Exception {
        LogEntry e = storer.moderGranted(current, "fido828@conference.jabber.ru/nick1");

        testParticipant(e, "moderGranted", EntryType.MODER_GRANTED);
    }

    @Test
    public void testModerRevoked() throws Exception {
        LogEntry e = storer.moderRevoked(current, "fido828@conference.jabber.ru/nick1");

        testParticipant(e, "moderRevoked", EntryType.MODER_REVOKED);
    }

    @Test
    public void testOwnerGranted() throws Exception {
        LogEntry e = storer.ownerGranted(current, "fido828@conference.jabber.ru/nick1");

        testParticipant(e, "ownerGranted", EntryType.OWNER_GRANTED);
    }

    @Test
    public void testOwnerRevoked() throws Exception {
        LogEntry e = storer.ownerRevoked(current, "fido828@conference.jabber.ru/nick1");

        testParticipant(e, "ownerRevoked", EntryType.OWNER_REVOKED);
    }

    @Test
    public void testAdminGranted() throws Exception {
        LogEntry e = storer.adminGranted(current, "fido828@conference.jabber.ru/nick1");

        testParticipant(e, "adminGranted", EntryType.ADMIN_GRANTED);
    }

    @Test
    public void testAdminRevoked() throws Exception {
        LogEntry e = storer.adminRevoked(current, "fido828@conference.jabber.ru/nick1");

        testParticipant(e, "adminRevoked", EntryType.ADMIN_REVOKED);
    }
}
