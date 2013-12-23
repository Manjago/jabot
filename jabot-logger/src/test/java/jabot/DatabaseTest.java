package jabot;

import jabot.dto.LogEntry;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.Date;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class DatabaseTest {
    @Test
    public void testInit() throws Exception {

        try(Database d = Database.init("jdbc:h2:mem:test", "sa", "sa");){
            d.check();
            d.check();
        }

    }

    @Test
    public void testStore() throws Exception {

        try(Database d = Database.init("jdbc:h2:mem:test", "sa", "sa");){
            d.check();

            DAOImpl dao = new DAOImpl();
            dao.setDb(d);

            LogEntry e = new LogEntry();
            e.setConference("testconf");
            e.setEventDate(new Date());
            e.setFrom("fromm");
            e.setText("texxt");

            TestCase.assertEquals(1L, dao.store(e));
        }


    }

    @Test
    public void testLoad() throws Exception {

        try(Database d = Database.init("jdbc:h2:mem:test", "sa", "sa");){
            d.check();

            DAOImpl dao = new DAOImpl();
            dao.setDb(d);

            LogEntry e = new LogEntry();
            e.setConference("testconf");
            e.setEventDate(new Date(2013, 1, 1));
            e.setFrom("fromm");
            e.setText("texxt");

            TestCase.assertEquals(1L, dao.store(e));

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

        try(Database d = Database.init("jdbc:h2:mem:test", "sa", "sa");){
            d.check();

            DAOImpl dao = new DAOImpl();
            dao.setDb(d);

            LogEntry e = new LogEntry();
            e.setConference("testconf");
            e.setEventDate(new Date(2013, 1, 1));
            e.setFrom("fromm");
            e.setText("texxt");

            TestCase.assertEquals(1L, dao.store(e));

            LogEntry loaded = dao.getById(2L);
            TestCase.assertNull(loaded);

        }


    }

}
