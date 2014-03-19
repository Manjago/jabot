package jabot.translator.commands;

import jabot.translator.dao.Transusers;
import jabot.translator.dto.TransUser;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

public class OperatorCommandParserImpl implements CommandParser {

    private final List<PatternParser> parsers;
    private final Transusers transusers;


    public OperatorCommandParserImpl(Transusers transusers) {
        this.transusers = transusers;
        parsers = Arrays.asList(
                new EnablePatternParser(),
                new DisablePatternParser()
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

    private class EnablePatternParser extends PatternParser {

        EnablePatternParser() {
            super("^!ON (\\S+)$");
        }

        @Override
        protected OperatorCmd execute(final String... args) {
            return new OperatorCmd() {

                @Override
                public String execute() {

                    final String jid = args[0];

                    TransUser t = transusers.updateIfExists(jid, true);
                    if (t == null) {
                        return MessageFormat.format("fail turn on translation for {0}", jid);
                    }
                    return MessageFormat.format("translation turned on for user {0}", t.displayString());
                }
            };
        }
    }

    private class DisablePatternParser extends PatternParser {

        DisablePatternParser() {
            super("^!OFF (\\S+)$");
        }

        @Override
        protected OperatorCmd execute(final String... args) {
            return new OperatorCmd() {

                @Override
                public String execute() {

                    final String jid = args[0];

                    TransUser t = transusers.updateIfExists(jid, false);
                    if (t == null) {
                        return MessageFormat.format("fail turn off translation for {0}", jid);
                    }
                    return MessageFormat.format("translation turned off for user {0}", t.displayString());
                }
            };
        }
    }

}
