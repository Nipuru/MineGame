package top.nipuru.minegame.server.game.offline;

import lombok.Getter;
import lombok.experimental.Accessors;
import top.nipuru.minegame.server.game.player.Data;
import top.nipuru.minegame.server.game.player.Table;
import top.nipuru.minegame.server.game.player.Unique;

/**
 * 离线消息是一种特殊玩家的数据，使用单独的方法新增，可以在玩家离线时新增
 * 只能在玩上线后处理并且删除
 */
@Getter
@Accessors(fluent = true)
@Table(name = "tb_offline", autoCreate = false)
public class OfflineData implements Data {
    /** 每条离线消息都有自己的 id */
    @Unique long id;

    /** 所属模块 */
    String module = EMPTY_STRING;

    /** 数据，很可能是json */
    String data = EMPTY_STRING;
}
