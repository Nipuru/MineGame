package top.nipuru.minegame.broker.exception;

import java.io.Serial;
import java.util.UUID;

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
