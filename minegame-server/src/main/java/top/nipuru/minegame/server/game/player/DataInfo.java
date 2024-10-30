package top.nipuru.minegame.server.game.player;

import lombok.Getter;
import top.nipuru.minegame.common.message.database.FieldMessage;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

@Getter
public class DataInfo {

    private final int playerId;
    private final int dbId;
    private final Map<String, List<List<FieldMessage>>> tables;

    public DataInfo(int playerId, int dbId, Map<String, List<List<FieldMessage>>> tables) {
        this.playerId = playerId;
        this.dbId = dbId;
        this.tables = tables;
    }


    @Nullable
    public <T extends Data> T unpack(Class<T> dataClass) throws Exception {
        return DataConvertor.unpack(tables, dataClass);
    }

    public <T extends Data> List<T> unpackList(Class<T> dataClass) throws Exception {
        return DataConvertor.unpackList(tables, dataClass);
    }

    public <T extends Data> void pack(T data) throws Exception {
        DataConvertor.pack(this.tables, data);
    }

}
