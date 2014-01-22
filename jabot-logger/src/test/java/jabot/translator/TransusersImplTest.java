package jabot.translator;

import jabot.db.Database;
import jabot.db.DatabaseFactory;
import jabot.translator.dto.TransUser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.*;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class TransusersImplTest {

    private DatabaseFactory dbF;
    private Database db;
    private Transusers ts;

    @Before
    public void setUp() throws Exception {
        dbF = new TranslatorDatabaseFactoryImpl("jdbc:h2:mem:test", "sa", "sa");
        db = dbF.create();
        ts = new TransusersImpl(db);
    }

    @After
    public void tearDown() throws Exception {
        ts.close();
    }

    @Test(expected = IllegalStateException.class)
    public void testClose() throws Exception {
        ts.close();
        db.getConnection();
    }

    @Test
    public void testIsOperator() throws Exception {
        assertFalse(ts.isOperator("test"));
        ts.createIfAbsent("test", true);
        assertTrue(ts.isOperator("test"));
    }

    @Test
    public void testIsOperatorForInactive() throws Exception {
        assertFalse(ts.isOperator("test"));
        ts.createIfAbsent("test", true);
        assertTrue(ts.isOperator("test"));
        ts.updateIfExists("test", false);
        assertFalse(ts.isOperator("test"));
    }

    @Test
    public void testGetOperators() throws Exception {
        ts.createIfAbsent("1", false);
        ts.createIfAbsent("2", true);
        ts.createIfAbsent("3", true);
        ts.createIfAbsent("4", false);
        List<String> ops = ts.getOperators();
        assertNotNull(ops);
        assertEquals(2, ops.size());
        assertEquals("2", ops.get(0));
        assertEquals("3", ops.get(1));
    }

    @Test
    public void testGetEmptyOperators() throws Exception {
        List<String> ops = ts.getOperators();
        assertNotNull(ops);
        assertEquals(0, ops.size());
    }

    @Test
    public void testCreateIfAbsent() throws Exception {
        assertFalse(ts.isOperator("1"));
        TransUser stored = ts.createIfAbsent("1", true);
        assertNotNull(stored);
        assertEquals("1", stored.getJid());
        assertEquals(1L, stored.getId());
        assertEquals(true, stored.isEnabled());

        TransUser stored2 = ts.createIfAbsent("1", false);
        assertEquals(stored, stored2);
    }

    @Test
    public void testDeleteIfExists() throws Exception {
        ts.createIfAbsent("1", true);
        ts.deleteIfExists("1");
        assertFalse(ts.isOperator("1"));
    }

    @Test
    public void testDeleteIfExistsNone() throws Exception {
        ts.deleteIfExists("1");
        assertFalse(ts.isOperator("1"));
    }

    @Test
    public void testUpdateIfExists() throws Exception {
        assertFalse(ts.isOperator("1"));
        TransUser stored = ts.createIfAbsent("1", true);
        assertNotNull(stored);
        assertEquals("1", stored.getJid());
        assertEquals(1L, stored.getId());
        assertEquals(true, stored.isEnabled());

        ts.updateIfExists("1", false);
        stored = ts.createIfAbsent("1", true);
        assertNotNull(stored);
        assertEquals("1", stored.getJid());
        assertEquals(1L, stored.getId());
        assertEquals(false, stored.isEnabled());

        ts.updateIfExists("2", true);
        stored = ts.createIfAbsent("1", true);
        assertNotNull(stored);
        assertEquals("1", stored.getJid());
        assertEquals(1L, stored.getId());
        assertEquals(false, stored.isEnabled());
    }
}
