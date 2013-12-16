package jabot;

import org.junit.Test;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class Addr3BadTest {

    @Test(expected = IllegalArgumentException.class)
    public void testNull() throws Exception {
         Addr3D.fromRaw(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBad() throws Exception {
        Addr3D.fromRaw("user/server/res");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBad2() throws Exception {
        Addr3D.fromRaw("user@server/res/res2");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBad3() throws Exception {
        Addr3D.fromRaw("user");
    }

}
