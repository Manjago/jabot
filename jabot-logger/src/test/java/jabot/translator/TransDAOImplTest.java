package jabot.translator;

import jabot.db.Database;
import jabot.db.DatabaseFactory;
import jabot.translator.dto.TransUser;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.*;


/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class TransDAOImplTest {

    private DatabaseFactory dbF;

    @Before
    public void setUp() throws Exception {
        dbF = new TranslatorDatabaseFactoryImpl("jdbc:h2:mem:test", "sa", "sa");
    }

    @Test
    public void testStore() throws Exception {
        TransUser user = new TransUser();
        user.setEnabled(true);
        user.setJid("kkk");

        try (Database db = dbF.create()) {
            TransDAO dao = new TransDAOImpl(db);

            dao.store(user);

            TransUser stored = dao.get("kkk");

            assertNotNull(stored);
            assertEquals(1L, stored.getId());
            assertEquals("kkk", stored.getJid());
            assertEquals(true, stored.isEnabled());
        }

    }

    @Test
    public void testUpdate() throws Exception {
        TransUser user = new TransUser();
        user.setEnabled(true);
        user.setJid("kkk");

        try (Database db = dbF.create()) {
            TransDAO dao = new TransDAOImpl(db);

            dao.store(user);

            TransUser stored = dao.get("kkk");

            stored.setEnabled(false);
            stored.setJid("ttt");

            dao.update(stored);

            TransUser loaded = dao.get(stored.getId());

            assertNotNull(loaded);
            assertEquals(stored, loaded);
        }

    }

    @Test
    public void testDelete() throws Exception {
        TransUser user = new TransUser();
        user.setEnabled(true);
        user.setJid("kkk");

        try (Database db = dbF.create()) {
            TransDAO dao = new TransDAOImpl(db);

            dao.store(user);

            TransUser stored = dao.get("kkk");

            dao.delete(stored.getJid());

            TransUser loaded = dao.get(stored.getId());

            assertTrue(loaded == null);
        }

    }

    @Test
    public void testGetAll() throws Exception {
        TransUser user = new TransUser();
        user.setEnabled(true);
        user.setJid("kkk");

        try (Database db = dbF.create()) {
            TransDAO dao = new TransDAOImpl(db);

            dao.store(user);

            TransUser stored = dao.get("kkk");

            List<TransUser> all = dao.getAll();
            assertNotNull(all);
            assertEquals(1, all.size());
            assertEquals(stored, all.get(0));
        }


    }
}
