package top.nipuru.minegame.common.message.shared;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@AllArgsConstructor
public class PlayerInfoUpdateNotify implements Serializable {
    @Serial
    private static final long serialVersionUID = -7666033328701471298L;
    private final PlayerInfoMessage playerInfo;
}
