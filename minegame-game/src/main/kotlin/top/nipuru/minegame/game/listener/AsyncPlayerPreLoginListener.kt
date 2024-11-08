package top.nipuru.minegame.game.listener

import net.afyer.afybroker.client.Broker
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import top.nipuru.minegame.common.message.PlayerDataMessage
import top.nipuru.minegame.common.message.PlayerDataTransferRequest
import top.nipuru.minegame.common.message.PlayerRegisterMessage
import top.nipuru.minegame.common.message.auth.QueryUserRequest
import top.nipuru.minegame.common.message.auth.UserMessage
import top.nipuru.minegame.common.message.database.QueryPlayerRequest
import top.nipuru.minegame.game.authRequest
import top.nipuru.minegame.game.databaseRequest
import top.nipuru.minegame.game.enableLatch
import top.nipuru.minegame.game.gameplay.player.DataInfo
import top.nipuru.minegame.game.gameplay.player.GamePlayer
import top.nipuru.minegame.game.logger
import java.net.InetAddress
import java.util.*

class AsyncPlayerPreLoginListener(private val pendingPlayers: MutableMap<UUID, GamePlayer>) : Listener {

    @EventHandler(priority = EventPriority.LOW)
    fun onEventLow(event: AsyncPlayerPreLoginEvent) {
        try {
            // 防止在插件启用之前玩家加入进来
            enableLatch.await()

            this.handlePreLogin(event.name, event.uniqueId, event.address)
        } catch (e: Exception) {
            val message = Component.text("登录失败！请重试").color(NamedTextColor.RED)
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, message)
            logger.error(e.message, e)
        }
    }

    private fun handlePreLogin(name: String, uniqueId: UUID, address: InetAddress) {
        // 优先从别的服务器转移数据
        val transferRequest = PlayerDataTransferRequest(uniqueId)
        val playerDataMessage = Broker.invokeSync<PlayerDataMessage>(transferRequest)

        if (playerDataMessage != null) {
            val dataInfo = DataInfo(playerDataMessage.data)
            val gamePlayer = GamePlayer(playerDataMessage.playerId, playerDataMessage.dbId, name, uniqueId)
            gamePlayer.unpack(dataInfo)
            pendingPlayers[uniqueId] = gamePlayer
        } else {
            // 其他服务器没有数据代表登录 需要新建数据或从数据库加载
            val queryUserRequest = QueryUserRequest(uniqueId, address.hostAddress)
            val userMessage = authRequest<UserMessage>(queryUserRequest)
            val queryPlayerRequest = QueryPlayerRequest(userMessage.playerId)
            val gamePlayer = GamePlayer(userMessage.playerId, userMessage.dbId, name, uniqueId)
            gamePlayer.preload(queryPlayerRequest)
            val dataInfo = DataInfo(databaseRequest(userMessage.dbId, queryPlayerRequest))
            gamePlayer.unpack(dataInfo)
            pendingPlayers[uniqueId] = gamePlayer
            Broker.oneway(PlayerRegisterMessage(uniqueId, gamePlayer.playerId))
        }
    }
}
