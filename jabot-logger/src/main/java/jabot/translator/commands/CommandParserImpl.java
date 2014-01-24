package jabot.translator.commands;

import jabot.Helper;
import jabot.translator.dao.Transusers;
import jabot.translator.dto.TransUser;

import java.text.MessageFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class CommandParserImpl implements CommandParser {

    private static final Pattern CREATE = Pattern.compile(
            "^%CREATE (\\S+)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern DELETE = Pattern.compile(
            "^%DELETE (\\S+)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern UPDATE = Pattern.compile(
            "^%UPDATE (\\S+) (\\d{1})$", Pattern.CASE_INSENSITIVE);
    private static final Pattern LIST = Pattern.compile(
            "^%LIST$", Pattern.CASE_INSENSITIVE);
    private Transusers transusers;

    public void setTransusers(Transusers transusers) {
        this.transusers = transusers;
    }

    @Override
    public OperatorCmd parse(String text) {


        if (Helper.isEmptyStr(text)) {
            return null;
        }

        if (isListCommand(text)) {
            return getListCmd();
        }

        Matcher m = CREATE.matcher(text);
        if (m.matches()) {
            return getCreateCmd(m.group(1));
        }

        m = DELETE.matcher(text);
        if (m.matches()) {
            return getDeleteCmd(m.group(1));
        }

        m = UPDATE.matcher(text);
        if (m.matches()) {
            return getUpdateCmd(m.group(1), Integer.valueOf(m.group(2)) != 0);
        }


        return null;
    }

    private OperatorCmd getListCmd() {
        return new OperatorCmd() {
            @Override
            public String execute() {
                List<String> data = transusers.getAll();

                if (data.size() == 0) {
                    return "empty list";
                }

                StringBuilder sb = new StringBuilder();
                for (String s : data) {
                    sb.append(s);
                    sb.append('\n');
                }

                return sb.toString();
            }
        };
    }

    private OperatorCmd getCreateCmd(final String jid) {
        return new OperatorCmd() {
            @Override
            public String execute() {
                TransUser t = transusers.createIfAbsent(jid, false);
                if (t == null) {
                    return MessageFormat.format("fail create user {0}", jid);
                }
                return MessageFormat.format("user {0}", t.displayString());
            }
        };
    }

    private OperatorCmd getDeleteCmd(final String jid) {
        return new OperatorCmd() {
            @Override
            public String execute() {
                transusers.deleteIfExists(jid);
                return MessageFormat.format("try delete user {0}", jid);
            }
        };
    }

    private OperatorCmd getUpdateCmd(final String jid, final boolean enable) {
        return new OperatorCmd() {
            @Override
            public String execute() {
                TransUser t = transusers.updateIfExists(jid, enable);
                if (t == null) {
                    return MessageFormat.format("fail update user {0}", jid);
                }
                return MessageFormat.format("updated user {0}", t.displayString());
            }
        };
    }

    private boolean isListCommand(String text) {
        return LIST.matcher(text).matches();
    }
}
