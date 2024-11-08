package top.nipuru.minegame.game.gameplay.player

import top.nipuru.minegame.game.logger
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class GamePlayerList {
    private val byUniqueId: MutableMap<UUID, GamePlayer> = ConcurrentHashMap()
    private val byPlayerId: MutableMap<Int, GamePlayer> = ConcurrentHashMap()

    fun tick() {
        val systemTimeMills = System.currentTimeMillis()
        for (player in byUniqueId.values) {
            player.tick(systemTimeMills)
        }
    }

    fun registerPlayer(player: GamePlayer) {
        if (logger.isDebugEnabled) {
            logger.debug("Register GamePlayer: {}", player.name)
        }
        byUniqueId[player.uniqueId] = player
        byPlayerId[player.playerId] = player
    }

    fun getPlayer(uniqueId: UUID): GamePlayer {
        val gamePlayer = byUniqueId[uniqueId]
            ?: throw NullPointerException("Player with uniqueId $uniqueId is not exist")
        return gamePlayer
    }

    fun getPlayer(playerId: Int): GamePlayer {
        val gamePlayer = byPlayerId[playerId]
            ?: throw NullPointerException("Player with playerId $playerId is not exist")
        return gamePlayer
    }

    val players: Collection<GamePlayer>
        get() = Collections.unmodifiableCollection(byUniqueId.values)

    fun removePlayer(player: GamePlayer) {
        byUniqueId.remove(player.uniqueId)
        byPlayerId.remove(player.playerId)
        if (logger.isDebugEnabled()) {
            logger.debug("Remove GamePlayer: {}", player.name)
        }
    }
}
