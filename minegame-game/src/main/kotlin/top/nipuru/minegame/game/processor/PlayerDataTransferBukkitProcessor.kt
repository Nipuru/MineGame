package top.nipuru.minegame.game.processor

import com.alipay.remoting.AsyncContext
import com.alipay.remoting.BizContext
import com.alipay.remoting.rpc.protocol.AsyncUserProcessor
import org.bukkit.Bukkit
import top.nipuru.minegame.common.message.PlayerDataMessage
import top.nipuru.minegame.common.message.PlayerDataTransferRequest
import top.nipuru.minegame.game.gameplay.player.DataInfo
import top.nipuru.minegame.game.gameplay.player.GamePlayer
import top.nipuru.minegame.game.logger
import top.nipuru.minegame.game.nms.freeze
import top.nipuru.minegame.game.nms.quit
import top.nipuru.minegame.game.playerList
import top.nipuru.minegame.game.plugin

class PlayerDataTransferBukkitProcessor : AsyncUserProcessor<PlayerDataTransferRequest>() {
    @Throws(Exception::class)
    override fun handleRequest(bizCtx: BizContext, asyncCtx: AsyncContext, request: PlayerDataTransferRequest) {
        Bukkit.getScheduler().runTask(plugin, Runnable {
            val player: GamePlayer = playerList.getPlayer(request.uniqueId)

            player.bukkitPlayer.freeze() // 冻结玩家 不处理客户端发包
            player.bukkitPlayer.quit()   // 强制移出玩家列表 触发 PlayerQuitEvent 并完成数据的保存

            val dataInfo = DataInfo(HashMap())

            try {
                player.pack(dataInfo)
            } catch (e: Exception) {
                logger.error(e.message, e)
                asyncCtx.sendException(e)
                return@Runnable
            }

            val response = PlayerDataMessage(player.playerId, player.dbId, dataInfo.tables)
            asyncCtx.sendResponse(response)
        })
    }

    override fun interest(): String {
        return PlayerDataTransferRequest::class.java.name
    }
}
