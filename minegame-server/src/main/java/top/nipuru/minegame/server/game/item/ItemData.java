package top.nipuru.minegame.server.game.item;

import lombok.Getter;
import lombok.experimental.Accessors;
import top.nipuru.minegame.server.game.player.Data;
import top.nipuru.minegame.server.game.player.Table;
import top.nipuru.minegame.server.game.player.Unique;

@Getter
@Accessors(fluent = true)
@Table(name = "tb_player")
public class ItemData implements Data {
    /** 物品类型 */
    @Unique int itemType;

    /** 物品id */
    @Unique int itemId;

    /** 数量 */
    long amount;
}
