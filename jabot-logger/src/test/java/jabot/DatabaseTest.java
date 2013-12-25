package jabot;

import jabot.dto.LogEntry;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class DatabaseTest {
    @Test
    public void testInit() throws Exception {

        try (Database d = Database.init("jdbc:h2:mem:test", "sa", "sa")) {
            d.check();
            d.check();
        }

    }

    @Test
    public void testStore() throws Exception {

        try (Database d = Database.init("jdbc:h2:mem:test", "sa", "sa")) {
            d.check();

            DAOImpl dao = new DAOImpl();
            dao.setDb(d);

            LogEntry e = new LogEntry();
            e.setConference("testconf");
            e.setEventDate(new Date());
            e.setFrom("fromm");
            e.setText("texxt");

            dao.store(e);

            LogEntry e2 = dao.getById(1L);
            TestCase.assertNotNull(e2);
            TestCase.assertEquals(1L, e2.getId());
        }


    }

    @Test
    public void testLoad() throws Exception {

        try (Database d = Database.init("jdbc:h2:mem:test", "sa", "sa")) {
            d.check();

            DAOImpl dao = new DAOImpl();
            dao.setDb(d);

            LogEntry e = new LogEntry();
            e.setConference("testconf");
            e.setEventDate(new Date(2013, 1, 1));
            e.setFrom("fromm");
            e.setText("texxt");

            dao.store(e);

            LogEntry loaded = dao.getById(1L);
            TestCase.assertNotNull(loaded);
            TestCase.assertEquals(1L, loaded.getId());
            TestCase.assertEquals("testconf", loaded.getConference());
            TestCase.assertEquals(new Date(2013, 1, 1), loaded.getEventDate());
            TestCase.assertEquals("fromm", loaded.getFrom());
            TestCase.assertEquals("texxt", loaded.getText());


        }


    }

    @Test
    public void testLoad2() throws Exception {

        try (Database d = Database.init("jdbc:h2:mem:test", "sa", "sa")) {
            d.check();

            DAOImpl dao = new DAOImpl();
            dao.setDb(d);

            LogEntry e = new LogEntry();
            e.setConference("testconf");
            e.setEventDate(new Date(2013, 1, 1));
            e.setFrom("fromm");
            e.setText("texxt");

            dao.store(e);

            LogEntry eLoaded = dao.getById(1L);
            TestCase.assertNotNull(eLoaded);
            TestCase.assertEquals(1L, eLoaded.getId());

            LogEntry loaded = dao.getById(2L);
            TestCase.assertNull(loaded);

        }


    }

    @Test
    public void testGetByPeriod() throws Exception {

        try (Database d = Database.init("jdbc:h2:mem:test", "sa", "sa")) {
            d.check();

            DAOImpl dao = new DAOImpl();
            dao.setDb(d);

            List<LogEntry> dataStore = new ArrayList<>();

            LogEntry e = new LogEntry();
            e.setConference("testconf");
            e.setEventDate(new Date(2013, 1, 1, 1, 1, 2));
            e.setFrom("fromm");
            e.setText("texxt");

            dataStore.add(e);

            LogEntry e2 = new LogEntry();
            e2.setConference("testconf");
            e2.setEventDate(new Date(2013, 1, 1, 1, 5, 2));
            e2.setFrom("fromm");
            e2.setText("texxt");

            dataStore.add(e2);

            dao.store(dataStore);

            List<LogEntry> data = dao.getByPeriod(new Date(2011, 1, 1, 1, 1, 2), new Date(2016, 1, 1, 1, 1, 2));
            TestCase.assertEquals(2, data.size());

            LogEntry loaded = data.get(1);
            TestCase.assertNotNull(loaded);
            TestCase.assertEquals(2L, loaded.getId());
            TestCase.assertEquals("testconf", loaded.getConference());
            TestCase.assertEquals(new Date(2013, 1, 1, 1, 5, 2), loaded.getEventDate());
            TestCase.assertEquals("fromm", loaded.getFrom());
            TestCase.assertEquals("texxt", loaded.getText());

            TestCase.assertEquals(0, dao.getByPeriod(new Date(2016, 1, 1, 1, 1, 2), new Date(2016, 1, 1, 1, 1, 2)).size());

            TestCase.assertEquals(2, dao.getByPeriod(new Date(2011, 1, 1, 1, 1, 2), new Date(2013, 1, 1, 1, 5, 2)).size());
            TestCase.assertEquals(1, dao.getByPeriod(new Date(2011, 1, 1, 1, 1, 2), new Date(2013, 1, 1, 1, 5, 1)).size());

        }

    }

    @Test
    public void testRegExp() throws Exception {

        try (Database d = Database.init("jdbc:h2:mem:test", "sa", "sa")) {
            d.check();

            DAOImpl dao = new DAOImpl();
            dao.setDb(d);

            List<LogEntry> dataStore = new ArrayList<>();
            for (int i = 0; i < 50; ++i) {
                LogEntry e2 = new LogEntry();
                e2.setConference("testconf");
                e2.setEventDate(new Date(2013, 1, 1, 1, 5, i));
                e2.setFrom("fromm" + i);
                e2.setText("texxt" + i);
                dataStore.add(e2);
            }
            dao.store(dataStore);

            List<LogEntry> data = dao.getByReg("xt1", 10);
            TestCase.assertEquals(10, data.size());
            TestCase.assertEquals(20, data.get(0).getId());
            TestCase.assertEquals("texxt19", data.get(0).getText());

        }

    }
}
