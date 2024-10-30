package top.nipuru.minegame.common.message.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class QueryUserRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final UUID uniqueId;
    private final String ip;

    @Getter
    @AllArgsConstructor
    public static class UserMessage implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
        private final int playerId;
        private final int dbId;
    }
}
