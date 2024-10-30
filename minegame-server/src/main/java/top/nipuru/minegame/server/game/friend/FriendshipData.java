package top.nipuru.minegame.server.game.friend;

import lombok.Getter;
import lombok.experimental.Accessors;
import top.nipuru.minegame.server.game.player.Data;
import top.nipuru.minegame.server.game.player.Table;
import top.nipuru.minegame.server.game.player.Unique;

@Getter
@Accessors(fluent = true)
@Table(name = "tb_friendship")
public class FriendshipData implements Data {

    /** 好友 id */
    @Unique int friendId;

    /** 建立时间 */
    long createTime;

    /** 备注信息 */
    String remark = EMPTY_STRING;
}
