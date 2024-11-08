package top.nipuru.minegame.game.processor

import com.alipay.remoting.AsyncContext
import com.alipay.remoting.BizContext
import com.alipay.remoting.rpc.protocol.AsyncUserProcessor
import org.bukkit.Bukkit
import top.nipuru.minegame.common.message.PlayerOfflineDataMessage
import top.nipuru.minegame.game.gameplay.player.GamePlayer
import top.nipuru.minegame.game.playerList
import top.nipuru.minegame.game.plugin

class PlayerOfflineDataBukkitProcessor : AsyncUserProcessor<PlayerOfflineDataMessage>() {

    override fun handleRequest(bizContext: BizContext, asyncContext: AsyncContext, request: PlayerOfflineDataMessage) {
        Bukkit.getScheduler().runTask(plugin, Runnable {
            val gamePlayer: GamePlayer = playerList.getPlayer(request.playerId)
            val handler = gamePlayer.offlineManager.getHandler(request.module)
            if (handler == null) {
                asyncContext.sendResponse(false)
                return@Runnable
            }
            val result = handler.handle(request.data, true)
            asyncContext.sendResponse(result)
        })
    }

    override fun interest(): String {
        return PlayerOfflineDataMessage::class.java.name
    }
}
