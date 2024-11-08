package top.nipuru.minegame.game.processor

import com.alipay.remoting.BizContext
import com.alipay.remoting.rpc.protocol.SyncUserProcessor
import org.bukkit.Bukkit
import top.nipuru.minegame.common.message.PlayerChatMessage
import top.nipuru.minegame.common.message.PlayerPrivateChatMessage
import top.nipuru.minegame.game.gameplay.player.GamePlayer
import top.nipuru.minegame.game.playerList

class PlayerPrivateChatServerProcessor : SyncUserProcessor<PlayerPrivateChatMessage>() {
    override fun handleRequest(bizContext: BizContext, request: PlayerPrivateChatMessage): Any {
        try {
            val bukkitPlayer = Bukkit.getPlayerExact(request.receiver)
            val player: GamePlayer = playerList.getPlayer(bukkitPlayer!!.uniqueId)
            val manager = player.chatManager
            if (!manager.couldReceivePrivate(request.sender)) {
                return PlayerPrivateChatMessage.DENY
            }
            manager.receivePrivate(request.sender, request.fragments)
        } catch (ignored: Exception) {
        } // 有概率玩家在跨服或者离线

        return PlayerPrivateChatMessage.SUCCESS
    }


    override fun interest(): String {
        return PlayerChatMessage::class.java.name
    }
}
