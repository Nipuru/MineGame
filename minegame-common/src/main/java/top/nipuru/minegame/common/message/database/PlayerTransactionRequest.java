package top.nipuru.minegame.common.message.database;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ToString
@Getter
public class PlayerTransactionRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -4741002006074319493L;
    private final int playerId;
    private final List<Delete> deletes;
    private final List<Update> updates;
    private final List<Insert> inserts;

    public PlayerTransactionRequest(int playerId) {
        this.playerId = playerId;
        this.deletes = new ArrayList<>();
        this.updates = new ArrayList<>();
        this.inserts = new ArrayList<>();
    }

    @ToString
    @Getter
    @AllArgsConstructor
    public static class Delete implements Serializable {
        @Serial
        private static final long serialVersionUID = -2231366846619423565L;
        private final String tableName;
        private final List<FieldMessage> uniqueFields;
    }

    @ToString
    @Getter
    @AllArgsConstructor
    public static class Insert implements Serializable {
        @Serial
        private static final long serialVersionUID = -4411950244042437856L;
        private final String tableName;
        private final List<FieldMessage> fields;
    }

    @ToString
    @Getter
    @AllArgsConstructor
    public static class Update implements Serializable {
        @Serial
        private static final long serialVersionUID = 748656432179210540L;
        private final String tableName;
        private final List<FieldMessage> uniqueFields;
        private final List<FieldMessage> updateFields;
    }

}
