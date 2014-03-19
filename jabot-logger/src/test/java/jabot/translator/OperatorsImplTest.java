package jabot.translator;

import jabot.db.Database;
import jabot.db.DatabaseFactory;
import jabot.translator.dao.Operators;
import jabot.translator.dao.OperatorsImpl;
import jabot.translator.dto.TransUser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.*;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class OperatorsImplTest {

    private DatabaseFactory dbF;
    private Database db;
    private Operators operators;

    @Before
    public void setUp() throws Exception {
        dbF = new TranslatorDatabaseFactoryImpl("jdbc:h2:mem:test", "sa", "sa");
        db = dbF.create();
        operators = new OperatorsImpl(db);
    }

    @After
    public void tearDown() throws Exception {
        operators.close();
    }

    @Test(expected = IllegalStateException.class)
    public void testClose() throws Exception {
        operators.close();
        db.getConnection();
    }

    @Test
    public void testIsOperator() throws Exception {
        assertFalse(operators.isActiveOperator("test"));
        operators.createIfAbsent("test", true);
        assertTrue(operators.isActiveOperator("test"));
    }

    @Test
    public void testIsOperatorForInactive() throws Exception {
        assertFalse(operators.isActiveOperator("test"));
        operators.createIfAbsent("test", true);
        assertTrue(operators.isActiveOperator("test"));
        operators.updateIfExists("test", false);
        assertFalse(operators.isActiveOperator("test"));
    }

    @Test
    public void testIsPassiveOperator() throws Exception {
        assertFalse(operators.isPassiveOperator("test"));
        operators.createIfAbsent("test", true);
        assertFalse(operators.isPassiveOperator("test"));
        operators.updateIfExists("test", false);
        assertTrue(operators.isPassiveOperator("test"));
    }

    @Test
    public void testGetOperators() throws Exception {
        operators.createIfAbsent("1", false);
        operators.createIfAbsent("2", true);
        operators.createIfAbsent("3", true);
        operators.createIfAbsent("4", false);
        List<String> ops = operators.getOperators();
        assertNotNull(ops);
        assertEquals(2, ops.size());
        assertEquals("2", ops.get(0));
        assertEquals("3", ops.get(1));
    }

    @Test
    public void testGetAll() throws Exception {
        operators.createIfAbsent("1", false);
        operators.createIfAbsent("2", true);
        operators.createIfAbsent("3", true);
        operators.createIfAbsent("4", false);
        List<String> users = operators.getAll();
        assertNotNull(users);
        assertEquals(4, users.size());
        assertEquals("1:false", users.get(0));
        assertEquals("2:true", users.get(1));
        assertEquals("3:true", users.get(2));
        assertEquals("4:false", users.get(3));
    }

    @Test
    public void testGetEmptyOperators() throws Exception {
        List<String> ops = operators.getOperators();
        assertNotNull(ops);
        assertEquals(0, ops.size());
    }

    @Test
    public void testCreateIfAbsent() throws Exception {
        assertFalse(operators.isActiveOperator("1"));
        TransUser stored = operators.createIfAbsent("1", true);
        assertNotNull(stored);
        assertEquals("1", stored.getJid());
        assertEquals(1L, stored.getId());
        assertEquals(true, stored.isEnabled());

        TransUser stored2 = operators.createIfAbsent("1", false);
        assertEquals(stored, stored2);
    }

    @Test
    public void testDeleteIfExists() throws Exception {
        operators.createIfAbsent("1", true);
        operators.deleteIfExists("1");
        assertFalse(operators.isActiveOperator("1"));
    }

    @Test
    public void testDeleteIfExistsNone() throws Exception {
        operators.deleteIfExists("1");
        assertFalse(operators.isActiveOperator("1"));
    }

    @Test
    public void testUpdateIfExists() throws Exception {
        assertFalse(operators.isActiveOperator("1"));
        TransUser stored = operators.createIfAbsent("1", true);
        assertNotNull(stored);
        assertEquals("1", stored.getJid());
        assertEquals(1L, stored.getId());
        assertEquals(true, stored.isEnabled());

        operators.updateIfExists("1", false);
        stored = operators.createIfAbsent("1", true);
        assertNotNull(stored);
        assertEquals("1", stored.getJid());
        assertEquals(1L, stored.getId());
        assertEquals(false, stored.isEnabled());

        operators.updateIfExists("2", true);
        stored = operators.createIfAbsent("1", true);
        assertNotNull(stored);
        assertEquals("1", stored.getJid());
        assertEquals(1L, stored.getId());
        assertEquals(false, stored.isEnabled());
    }
}
