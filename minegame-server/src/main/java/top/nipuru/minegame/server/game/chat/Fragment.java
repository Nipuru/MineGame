package top.nipuru.minegame.server.game.chat;

import java.io.Serializable;

public record Fragment(Serializable... args) {

    @SuppressWarnings("unchecked")
    public <T> T getArg(int index) {
        return (T) args[index];
    }
}
