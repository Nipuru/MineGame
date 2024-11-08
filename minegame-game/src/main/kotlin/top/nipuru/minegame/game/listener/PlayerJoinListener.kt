package top.nipuru.minegame.game.listener

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import top.nipuru.minegame.game.constants.DAY
import top.nipuru.minegame.game.dayZero
import top.nipuru.minegame.game.gameplay.player.GamePlayer
import top.nipuru.minegame.game.logger
import top.nipuru.minegame.game.playerList
import java.util.*

class PlayerJoinListener(private val pendingPlayers: MutableMap<UUID, GamePlayer>) : Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    fun onEventLowest(event: PlayerJoinEvent) {
        event.joinMessage(null)

        val player: Player = event.getPlayer()
        try {
            handleJoin(player)
        } catch (e: Exception) {
            val message = Component.text("加入游戏失败！请重试").color(NamedTextColor.RED)
            player.kick(message)
            logger.error(e.message, e)
        }
    }

    @Throws(Exception::class)
    private fun handleJoin(player: Player) {
        // GamePlayer 在 PreLogin 的时候就应该加载好了
        val gamePlayer = pendingPlayers.remove(player.uniqueId)
            ?: throw NullPointerException("Player " + player.name + " has no pending data")

        playerList.registerPlayer(gamePlayer)
        gamePlayer.init()

        // 玩家登录
        if (!gamePlayer.coreManager.isOnline) onLogin(gamePlayer)

        // 玩家加入服务器
        gamePlayer.onJoin()
    }

    private fun onLogin(gamePlayer: GamePlayer) {
        val coreManager = gamePlayer.coreManager
        coreManager.isOnline = true
        if (coreManager.resetTime < dayZero()) {
            var lastResetTime: Long = coreManager.resetTime
            // 代表是新玩家
            if (lastResetTime == 0L) {
                gamePlayer.initNew()
                gamePlayer.onNewDay(dayZero())
            } else {
                // 最多重置30天
                if (dayZero() - lastResetTime > 30 * DAY) {
                    lastResetTime = dayZero() - 30 * DAY
                }
                while (lastResetTime + DAY <= dayZero()) {
                    lastResetTime += DAY
                    gamePlayer.onNewDay(lastResetTime)
                }
            }
        }
        gamePlayer.onLogin()
    }
}
