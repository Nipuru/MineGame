package top.nipuru.minegame.game.listener

import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import top.nipuru.minegame.game.logger
import top.nipuru.minegame.game.playerList
import top.nipuru.minegame.game.plugin


class PlayerChatListener : Listener {
    @EventHandler
    fun onEvent(event: AsyncChatEvent) {
        event.isCancelled = true
        val message: String = LegacyComponentSerializer.legacySection().serialize(event.message())
        try {
            handleChat(event.player, message)
        } catch (e: java.lang.Exception) {
            logger.error(e.message, e)
        }
    }

    private fun handleChat(bukkitPlayer: org.bukkit.entity.Player, message: String) {
        val player = playerList.getPlayer(bukkitPlayer.uniqueId)
        Bukkit.getScheduler().runTask(plugin, Runnable {
            if (player.chatManager.isMuted) {
                return@Runnable
            }
            if (player.chatManager.hasMsgTarget()) {
                player.chatManager.sendPrivate(player.chatManager.msgTarget, message)
            } else {
                player.chatManager.sendPublic(message)
            }
        })
    }
}
