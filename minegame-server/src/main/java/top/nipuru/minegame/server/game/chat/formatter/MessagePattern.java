package top.nipuru.minegame.server.game.chat.formatter;

import java.util.regex.Pattern;

public abstract class MessagePattern implements MessageFormatter {

    private final Pattern pattern;

    public MessagePattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public Pattern getPattern() {
        return pattern;
    }
}
