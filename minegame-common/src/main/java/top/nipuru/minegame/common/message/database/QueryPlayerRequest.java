package top.nipuru.minegame.common.message.database;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
public class QueryPlayerRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 8340998213700235733L;

    private final int playerId;
    private final List<TableInfo> tables;

    public QueryPlayerRequest(int playerId) {
        this.playerId = playerId;
        this.tables = new ArrayList<>();
    }

    @Getter
    @AllArgsConstructor
    public static class TableInfo implements Serializable {
        @Serial
        private static final long serialVersionUID = -6850621338250505920L;

        private final String tableName;
        private final boolean autoCreate;
        private final Map<String, Class<?>> fields;
        private final List<String> uniqueKeys;
    }
}
