package jabot.translator.commands;

import jabot.translator.dao.Transusers;
import jabot.translator.dto.TransUser;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class CommandParserImpl implements CommandParser {

    private final List<PatternParser> parsers;
    private final Transusers transusers;

    public CommandParserImpl(Transusers transusers) {
        this.transusers = transusers;
        parsers = Arrays.asList(
                new CreatePatternParser(),
                new DeletePatternParser(),
                new UpdatePatternParser(),
                new ListPatternParser()
        );
    }

    @Override
    public OperatorCmd parse(String text) {

        for (PatternParser patternParser : parsers) {
            OperatorCmd cmd = patternParser.exec(text);
            if (cmd != null) {
                return cmd;
            }
        }

        return null;

    }

    private class CreatePatternParser extends PatternParser {
        public CreatePatternParser() {
            super("^%CREATE (\\S+)$");
        }

        @Override
        protected OperatorCmd execute(String... args) {

            final String jid = args[0];

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
    }

    private class DeletePatternParser extends PatternParser {

        public DeletePatternParser() {
            super("^%DELETE (\\S+)$");
        }

        @Override
        protected OperatorCmd execute(String... args) {
            final String jid = args[0];

            return new OperatorCmd() {
                @Override
                public String execute() {
                    transusers.deleteIfExists(jid);
                    return MessageFormat.format("try delete user {0}", jid);
                }
            };
        }
    }

    private class UpdatePatternParser extends PatternParser {

        public UpdatePatternParser() {
            super("^%UPDATE (\\S+) (\\d{1})$");
        }

        @Override
        protected OperatorCmd execute(String... args) {
            final String jid = args[0];
            final boolean enable = Integer.parseInt(args[1]) != 0;

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
    }

    private class ListPatternParser extends PatternParser {
        public ListPatternParser() {
            super("^%LIST$");
        }

        @Override
        protected OperatorCmd execute(String... args) {
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
    }
}
