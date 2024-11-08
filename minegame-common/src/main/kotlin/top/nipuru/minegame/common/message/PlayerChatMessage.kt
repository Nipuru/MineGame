package top.nipuru.minegame.common.message

import top.nipuru.minegame.common.message.shared.PlayerInfoMessage
import java.io.Serializable

/** 公共聊天消息  */
class PlayerChatMessage(val sender: PlayerInfoMessage, val fragments: Array<FragmentMessage>) : Serializable {
    companion object {
        const val SUCCESS: Int = 0
        const val FAILURE: Int = 1
        const val RATE_LIMIT: Int = 3
    }
}
