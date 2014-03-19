package jabot.translator.commands;

import jabot.translator.dto.TransUser;
import junit.framework.TestCase;
import org.junit.Test;

public class OperatorCommandParserImplTest {
    @Test
    public void testParseOn() throws Exception {
        CommandParser parser = new OperatorCommandParserImpl(new TransusersWrapper() {

            @Override
            public TransUser updateIfExists(String jid, boolean enabled) {
                TransUser u = new TransUser();
                u.setEnabled(enabled);
                u.setJid(jid);
                return u;
            }
        });

        OperatorCmd cmd = parser.parse("!ON user@ttt.test");
        TestCase.assertNotNull(cmd);
        TestCase.assertEquals("translation turned on for user user@ttt.test:true", cmd.execute());

    }

    @Test
    public void testParseOff() throws Exception {
        CommandParser parser = new OperatorCommandParserImpl(new TransusersWrapper() {

            @Override
            public TransUser updateIfExists(String jid, boolean enabled) {
                TransUser u = new TransUser();
                u.setEnabled(enabled);
                u.setJid(jid);
                return u;
            }
        });

        OperatorCmd cmd = parser.parse("!OFF user@ttt.test");
        TestCase.assertNotNull(cmd);
        TestCase.assertEquals("translation turned off for user user@ttt.test:false", cmd.execute());

    }

}
