package top.nipuru.minegame.server.game.player;

import lombok.Getter;

@Getter
public class DataAction {

    private final Type type;
    private final Object data;
    private final String[] fields;

    public DataAction(Type type, Object data, String[] fields) {
        this.type = type;
        this.data = data;
        this.fields = fields;
    }

    public enum Type {
        INSERT, UPDATE, DELETE
    }

}
