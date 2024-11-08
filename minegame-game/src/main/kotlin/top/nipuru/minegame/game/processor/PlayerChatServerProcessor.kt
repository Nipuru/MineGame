package top.nipuru.minegame.game.processor

import com.alipay.remoting.AsyncContext
import com.alipay.remoting.BizContext
import com.alipay.remoting.rpc.protocol.AsyncUserProcessor
import top.nipuru.minegame.common.message.PlayerChatMessage
import top.nipuru.minegame.game.BukkitPlugin
import top.nipuru.minegame.game.playerList

class PlayerChatServerProcessor : AsyncUserProcessor<PlayerChatMessage>() {

    override fun handleRequest(bizContext: BizContext, asyncContext: AsyncContext, request: PlayerChatMessage) {
        for (player in playerList.players) {
            val manager = player.chatManager
            manager.receivePublic(request.sender, request.fragments)
        }
    }

    override fun interest(): String {
        return PlayerChatMessage::class.java.name
    }
}
