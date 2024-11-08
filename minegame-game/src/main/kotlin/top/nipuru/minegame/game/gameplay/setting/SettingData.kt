package top.nipuru.minegame.game.gameplay.setting

import top.nipuru.minegame.game.gameplay.player.Data
import top.nipuru.minegame.game.gameplay.player.Table
import top.nipuru.minegame.game.gameplay.player.Unique

@Table(name = "tb_setting")
class SettingData : Data {
    /** 设置 id  */
    @JvmField
    @Unique
    var id: Int = 0

    /** 选项值  */
    var option: Int = 0
}
