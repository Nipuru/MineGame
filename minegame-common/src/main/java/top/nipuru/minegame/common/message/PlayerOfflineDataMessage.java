package top.nipuru.minegame.common.message;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlayerOfflineDataMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    int playerId;
    int dbId;
    String module;
    String data;

}
