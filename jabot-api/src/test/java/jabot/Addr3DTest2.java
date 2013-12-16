package jabot;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class Addr3DTest2 {

    private Addr3D a;

    @Before
    public void setUp() throws Exception {
        a = Addr3D.fromRaw("user@server");
    }

    @Test
    public void testGetName() throws Exception {
        TestCase.assertEquals("user", a.getName());
    }

    @Test
    public void testGetServer() throws Exception {
        TestCase.assertEquals("server", a.getServer());
    }

    @Test
    public void testGetResource() throws Exception {
        TestCase.assertEquals("", a.getResource());
    }

    @Test
    public void testNameServer() throws Exception {
        TestCase.assertEquals("user@server", a.getNameServer());
    }

    @Test
    public void testFull() throws Exception {
        TestCase.assertEquals("user@server/res", a.getFullName());
    }
}
