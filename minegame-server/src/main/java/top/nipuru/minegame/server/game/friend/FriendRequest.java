package top.nipuru.minegame.server.game.friend;

import lombok.Getter;
import lombok.experimental.Accessors;
import top.nipuru.minegame.server.game.player.Data;
import top.nipuru.minegame.server.game.player.Table;
import top.nipuru.minegame.server.game.player.Unique;

@Getter
@Accessors(fluent = true)
@Table(name = "tb_friend_request")
public class FriendRequest implements Data {
    /** 玩家 id */
    @Unique int friendId;

    /** 建立时间 */
    long createTime;
}
