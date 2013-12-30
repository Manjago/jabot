package jabot;

import jabot.logger.DAOImpl;
import jabot.logger.Database;
import jabot.logger.dto.EntryType;
import jabot.logger.dto.LogEntry;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static junit.framework.TestCase.*;

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
            e.setEntryType(EntryType.MSG);

            dao.store(e);

            LogEntry e2 = dao.getById(1L);
            assertNotNull(e2);
            assertEquals(1L, e2.getId());
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
            e.setFromMe(true);
            e.setEntryType(EntryType.MSG);

            dao.store(e);

            LogEntry loaded = dao.getById(1L);
            assertNotNull(loaded);
            assertEquals(1L, loaded.getId());
            assertEquals("testconf", loaded.getConference());
            assertEquals(new Date(2013, 1, 1), loaded.getEventDate());
            assertEquals("fromm", loaded.getFrom());
            assertEquals("texxt", loaded.getText());
            assertEquals(true, loaded.isFromMe());
            assertEquals(EntryType.MSG, loaded.getEntryType());
        }


    }

    @Test
    public void testLoadDelay() throws Exception {

        try (Database d = Database.init("jdbc:h2:mem:test", "sa", "sa")) {
            d.check();

            DAOImpl dao = new DAOImpl();
            dao.setDb(d);

            LogEntry e = new LogEntry();
            e.setConference("testconf");
            e.setEventDate(new Date(2013, 1, 1));
            e.setFrom("fromm");
            e.setText("texxt");
            e.setFromMe(true);
            e.setDelayDate(new Date(2013, 1, 1, 5, 6, 7));
            e.setEntryType(EntryType.DELAYMSG);

            dao.store(e);

            LogEntry loaded = dao.getById(1L);
            assertNotNull(loaded);
            assertEquals(1L, loaded.getId());
            assertEquals("testconf", loaded.getConference());
            assertEquals(new Date(2013, 1, 1), loaded.getEventDate());
            assertEquals("fromm", loaded.getFrom());
            assertEquals("texxt", loaded.getText());
            assertEquals(true, loaded.isFromMe());
            assertEquals(EntryType.DELAYMSG, loaded.getEntryType());
            assertEquals(new Date(2013, 1, 1, 5, 6, 7), loaded.getDelayDate());
        }


    }

    @Test(expected = IllegalArgumentException.class)
    public void testStoreBadDelay() throws Exception {

        try (Database d = Database.init("jdbc:h2:mem:test", "sa", "sa")) {
            d.check();

            DAOImpl dao = new DAOImpl();
            dao.setDb(d);

            LogEntry e = new LogEntry();
            e.setConference("testconf");
            e.setEventDate(new Date(2013, 1, 1));
            e.setFrom("fromm");
            e.setText("texxt");
            e.setEntryType(EntryType.DELAYMSG);

            dao.store(e);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStoreBadMsg() throws Exception {

        try (Database d = Database.init("jdbc:h2:mem:test", "sa", "sa")) {
            d.check();

            DAOImpl dao = new DAOImpl();
            dao.setDb(d);

            LogEntry e = new LogEntry();
            e.setConference("testconf");
            e.setEventDate(new Date(2013, 1, 1));
            e.setFrom("fromm");
            e.setText("texxt");
            e.setDelayDate(new Date());
            e.setEntryType(EntryType.MSG);

            dao.store(e);
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
            e.setEntryType(EntryType.MSG);

            dao.store(e);

            LogEntry eLoaded = dao.getById(1L);
            assertNotNull(eLoaded);
            assertEquals(1L, eLoaded.getId());

            LogEntry loaded = dao.getById(2L);
            assertNull(loaded);

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
            e.setFromMe(false);
            e.setEntryType(EntryType.MSG);

            dataStore.add(e);

            LogEntry e2 = new LogEntry();
            e2.setConference("testconf");
            e2.setEventDate(new Date(2013, 1, 1, 1, 5, 2));
            e2.setFrom("fromm");
            e2.setText("texxt");
            e2.setFromMe(true);
            e2.setEntryType(EntryType.MSG);

            dataStore.add(e2);

            dao.store(dataStore);

            List<LogEntry> data = dao.getByPeriod(new Date(2011, 1, 1, 1, 1, 2), new Date(2016, 1, 1, 1, 1, 2));
            assertEquals(2, data.size());

            LogEntry loaded = data.get(1);
            assertNotNull(loaded);
            assertEquals(2L, loaded.getId());
            assertEquals("testconf", loaded.getConference());
            assertEquals(new Date(2013, 1, 1, 1, 5, 2), loaded.getEventDate());
            assertEquals("fromm", loaded.getFrom());
            assertEquals("texxt", loaded.getText());
            assertEquals(true, loaded.isFromMe());
            assertEquals(EntryType.MSG, loaded.getEntryType());

            assertEquals(0, dao.getByPeriod(new Date(2016, 1, 1, 1, 1, 2), new Date(2016, 1, 1, 1, 1, 2)).size());

            assertEquals(2, dao.getByPeriod(new Date(2011, 1, 1, 1, 1, 2), new Date(2013, 1, 1, 1, 5, 2)).size());
            assertEquals(1, dao.getByPeriod(new Date(2011, 1, 1, 1, 1, 2), new Date(2013, 1, 1, 1, 5, 1)).size());

        }

    }

    @Test
    public void testGetByPeriodDelay() throws Exception {

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
            e.setFromMe(false);
            e.setDelayDate(new Date(2013, 1, 1, 1, 1, 2));
            e.setEntryType(EntryType.DELAYMSG);

            dataStore.add(e);

            dao.store(dataStore);

            List<LogEntry> data = dao.getByPeriod(new Date(2011, 1, 1, 1, 1, 2), new Date(2016, 1, 1, 1, 1, 2));
            assertEquals(0, data.size());

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
                e2.setEntryType(EntryType.MSG);
                dataStore.add(e2);
            }
            dao.store(dataStore);

            List<LogEntry> data = dao.getByReg("xt1", 10);
            assertEquals(10, data.size());
            assertEquals(20, data.get(0).getId());
            assertEquals("texxt19", data.get(0).getText());
            assertEquals(EntryType.MSG, data.get(0).getEntryType());
        }

    }

    @Test
    public void testRegExpDelay() throws Exception {

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
                e2.setDelayDate(new Date(2013, 1, 1, 1, 5, i));
                e2.setEntryType(EntryType.DELAYMSG);
                dataStore.add(e2);
            }
            for (int i = 0; i < 50; ++i) {
                LogEntry e2 = new LogEntry();
                e2.setConference("testconf");
                e2.setEventDate(new Date(2013, 1, 1, 1, 5, i));
                e2.setFrom("fromm" + i);
                e2.setText("texxt" + i);
                e2.setEntryType(EntryType.MSG);
                dataStore.add(e2);
            }
            dao.store(dataStore);

            List<LogEntry> data = dao.getByReg("xt1", 10);
            assertEquals(10, data.size());
            assertEquals(70, data.get(0).getId());
            assertEquals("texxt19", data.get(0).getText());
            assertEquals(EntryType.MSG, data.get(0).getEntryType());
        }

    }

}
