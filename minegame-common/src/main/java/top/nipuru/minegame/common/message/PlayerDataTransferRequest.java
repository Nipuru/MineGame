package top.nipuru.minegame.common.message;

import top.nipuru.minegame.common.message.database.FieldMessage;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlayerDataTransferRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -7034910255992654470L;

    UUID uniqueId;

    @Getter
    @Setter
    @Accessors(chain = true)
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class PlayerDataMessage implements Serializable {
        @Serial
        private static final long serialVersionUID = -3924506521057659722L;

        int playerId;
        int dbId;
        Map<String, List<List<FieldMessage>>> data;
    }
}
