package top.nipuru.minegame.common.message;

import top.nipuru.minegame.common.message.shared.PlayerInfoMessage;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;

/** 公共聊天消息 */
@Getter
@Setter
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlayerChatMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 3486916666371981851L;
    public static final int SUCCESS = 0;
    public static final int FAILURE = 1;
    public static final int RATE_LIMIT = 3;

    PlayerInfoMessage sender;
    FragmentMessage[] fragments;
}
