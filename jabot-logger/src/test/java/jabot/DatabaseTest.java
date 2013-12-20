package jabot;

import org.junit.Test;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class DatabaseTest {
    @Test
    public void testInit() throws Exception {

        try(Database d = Database.init("jdbc:h2:mem:test", "sa", "sa");){
            d.executeSql("CREATE TABLE LOGDATA\n" +
                    "(\n" +
                    "    ID BIGINT PRIMARY KEY NOT NULL,\n" +
                    "    TEXT CLOB\n" +
                    ");\n");


        }


    }
}
