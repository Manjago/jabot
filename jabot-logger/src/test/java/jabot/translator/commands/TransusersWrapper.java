package jabot.translator.commands;

import jabot.translator.dao.Transusers;
import jabot.translator.dto.TransUser;

import java.util.List;

public class TransusersWrapper implements Transusers {
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
