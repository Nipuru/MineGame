package top.nipuru.minegame.game.gameplay.chat

import top.nipuru.minegame.game.gameplay.player.Data
import top.nipuru.minegame.game.gameplay.player.Table
import top.nipuru.minegame.game.gameplay.player.Temp

@Table(name = "tb_chat")
class ChatData : Data {
    /** 禁言截止时间  */
    var mute: Long = 0

    /** 私聊玩家名  */
    @Temp
    var msgTarget: String = ""
}
