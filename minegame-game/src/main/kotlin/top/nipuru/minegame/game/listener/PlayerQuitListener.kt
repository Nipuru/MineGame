package top.nipuru.minegame.game.listener

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import top.nipuru.minegame.game.gameplay.player.GamePlayer
import top.nipuru.minegame.game.nms.hasDisconnected
import top.nipuru.minegame.game.playerList

class PlayerQuitListener : Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    fun onEventMonitor(event: PlayerQuitEvent) {
        event.quitMessage(null)
        val player = event.player
        val gamePlayer: GamePlayer = playerList.getPlayer(player.uniqueId)
        // 由于存在跨服机制 所以 Quit 可能被触发多次 但只有第一次有效
        if (player.isOnline) handleQuit(player, gamePlayer)
    }

    private fun handleQuit(player: Player, gamePlayer: GamePlayer) {
        // 玩家退出方法
        gamePlayer.onQuit()

        // 判断玩家是否断开连接（跨服不会断开连接 因为是被强制移出玩家列表 并没有断连）
        if (player.hasDisconnected()) {
            gamePlayer.coreManager.isOnline = false
            gamePlayer.onLogout()
        }

        // 最后再调用一次 tick 方法
        gamePlayer.tick(System.currentTimeMillis())

        playerList.removePlayer(gamePlayer)
    }
}
