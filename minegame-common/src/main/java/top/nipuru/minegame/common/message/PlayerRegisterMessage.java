package top.nipuru.minegame.common.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class PlayerRegisterMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = -7922516683486954062L;
    private final UUID uniqueId;
    private final int playerId;
}
