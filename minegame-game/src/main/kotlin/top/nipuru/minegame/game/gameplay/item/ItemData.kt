package top.nipuru.minegame.game.gameplay.item

import top.nipuru.minegame.game.gameplay.player.Data
import top.nipuru.minegame.game.gameplay.player.Table
import top.nipuru.minegame.game.gameplay.player.Unique

@Table(name = "tb_player")
class ItemData : Data {
    /** 物品类型  */
    @Unique
    var itemType: Int = 0

    /** 物品id  */
    @Unique
    var itemId: Int = 0

    /** 数量  */
    var amount: Long = 0
}
