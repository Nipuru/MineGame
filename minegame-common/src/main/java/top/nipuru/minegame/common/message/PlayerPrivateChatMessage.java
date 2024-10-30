package top.nipuru.minegame.common.message;

import top.nipuru.minegame.common.message.shared.PlayerInfoMessage;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;

/** 私聊消息 */
@Getter
@Setter
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlayerPrivateChatMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 5232733251148277916L;
    public static final int SUCCESS = 0;
    public static final int FAILURE = 1;
    public static final int RATE_LIMIT = 2;
    public static final int NOT_ONLINE = 3;
    public static final int DENY = 4;

    PlayerInfoMessage sender;
    FragmentMessage[] fragments;
    String receiver;
}