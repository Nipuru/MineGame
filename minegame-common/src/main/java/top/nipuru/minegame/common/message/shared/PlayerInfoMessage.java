package top.nipuru.minegame.common.message.shared;

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
public class PlayerInfoMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = -7244032150500539776L;

    /** 玩家id */
    int playerId;

    /** 玩家名字 */
    String name;

    /** dbId */
    int dbId;

    /** 货币 */
    long coin;

    /** 头衔id */
    int rankId;

    /** 创建时间 */
    long createTime;

    /** 最后离线时间 */
    long lastLogoutTime;

    /** 累计在线时间 */
    long playedTime;
}
