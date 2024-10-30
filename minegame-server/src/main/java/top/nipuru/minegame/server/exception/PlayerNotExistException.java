package top.nipuru.minegame.server.exception;

import java.io.Serial;
import java.util.UUID;

/**
 * 正常情况下 BukkitPlayer 和 GamePlayer 应该同时存在，或者同时不存在
 * 异步切换的时候，可能会抛出此异常，应当捕获然后 ignore
 */
public class PlayerNotExistException extends IgnoredException {
    @Serial
    private static final long serialVersionUID = 1L;

    public PlayerNotExistException(UUID uniqueId) {
        super("Player with uniqueId: " + uniqueId + " is not exist");
    }

    public PlayerNotExistException(int playerId) {
        super("Player with playerId: " + playerId + " is not exist");
    }

}
