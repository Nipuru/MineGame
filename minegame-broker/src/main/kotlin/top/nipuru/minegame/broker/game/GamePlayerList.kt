package top.nipuru.minegame.broker.game

import top.nipuru.minegame.broker.logger
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class GamePlayerList {
    private val byUniqueId: MutableMap<UUID, GamePlayer> = ConcurrentHashMap()
    private val byPlayerId: MutableMap<Int, GamePlayer> = ConcurrentHashMap()

    fun registerPlayer(player: GamePlayer) {
        if (logger.isDebugEnabled) {
            logger.debug("Register GamePlayer: {}", player.name)
        }
        byUniqueId[player.uniqueId] = player
        byPlayerId[player.playerId] = player
    }

    fun getPlayer(uniqueId: UUID): GamePlayer {
        val gamePlayer = byUniqueId[uniqueId] ?: throw NullPointerException()
        return gamePlayer
    }

    fun getPlayer(playerId: Int): GamePlayer {
        val gamePlayer = byPlayerId[playerId] ?: throw NullPointerException()
        return gamePlayer
    }

    val players: Collection<GamePlayer>
        get() = Collections.unmodifiableCollection(byUniqueId.values)

    fun removePlayer(player: GamePlayer) {
        byUniqueId.remove(player.uniqueId)
        byPlayerId.remove(player.playerId)
        if (logger.isDebugEnabled) {
            logger.debug("Remove GamePlayer: {}", player.name)
        }
    }
}
