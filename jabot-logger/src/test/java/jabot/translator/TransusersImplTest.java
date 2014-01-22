package jabot.translator;

import jabot.db.Database;
import jabot.db.DatabaseFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

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

    }

    @Test
    public void testCreateIfAbsent() throws Exception {


    }

    @Test
    public void testDeleteIfExists() throws Exception {


    }

    @Test
    public void testUpdateIfExists() throws Exception {


    }
}
