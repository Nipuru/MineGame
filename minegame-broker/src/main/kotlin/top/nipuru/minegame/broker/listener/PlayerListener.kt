package top.nipuru.minegame.broker.listener

import net.afyer.afybroker.server.event.PlayerProxyLogoutEvent
import net.afyer.afybroker.server.plugin.EventHandler
import net.afyer.afybroker.server.plugin.Listener
import top.nipuru.minegame.broker.game.GamePlayer
import top.nipuru.minegame.broker.playerList

class PlayerListener : Listener {
    @EventHandler
    fun onEvent(event: PlayerProxyLogoutEvent) {
        try {
            val player: GamePlayer = playerList.getPlayer(event.player.uniqueId)
            playerList.removePlayer(player)
        } catch (_: Exception) {}
    }
}
