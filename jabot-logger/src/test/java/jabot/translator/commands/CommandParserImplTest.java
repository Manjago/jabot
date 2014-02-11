package jabot.translator.commands;

import jabot.translator.dao.Transusers;
import jabot.translator.dto.TransUser;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.List;

/**
 * @author Manjago (kirill@temnenkov.com)
 */
public class CommandParserImplTest {

    @Test
    public void testParseCreate() throws Exception {
        CommandParser parser = new CommandParserImpl(new TransusersWrapper() {
            @Override
            public TransUser createIfAbsent(String jid, boolean enabled) {
                TransUser user = new TransUser();
                user.setJid(jid);
                user.setEnabled(enabled);
                user.setId(1L);
                return user;
            }
        });

        OperatorCmd cmd = parser.parse("!CREATE user@ttt.test");
        TestCase.assertNotNull(cmd);
        TestCase.assertEquals("user user@ttt.test:false", cmd.execute());
    }

    private class TransusersWrapper implements Transusers {

        @Override
        public List<String> getAll() {
            return null;
        }

        @Override
        public TransUser createIfAbsent(String jid, boolean enabled) {
            return null;
        }

        @Override
        public void deleteIfExists(String jid) {

        }

        @Override
        public TransUser updateIfExists(String jid, boolean enabled) {
            return null;
        }

        @Override
        public void close() throws Exception {

        }
    }
}
