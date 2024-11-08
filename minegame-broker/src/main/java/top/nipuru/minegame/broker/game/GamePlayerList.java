package top.nipuru.minegame.broker.game;

import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class GamePlayerList {

    private final Map<UUID, GamePlayer> byUniqueId = new ConcurrentHashMap<>();
    private final Map<Integer, GamePlayer> byPlayerId = new ConcurrentHashMap<>();

    public GamePlayerList() {
    }

    public void registerPlayer(GamePlayer player) {
        if (log.isDebugEnabled()) {
            log.debug("Register GamePlayer: {}", player.getName());
        }
        byUniqueId.put(player.getUniqueId(), player);
        byPlayerId.put(player.getPlayerId(), player);
    }

    public GamePlayer getPlayer(UUID uniqueId) {
        GamePlayer gamePlayer = byUniqueId.get(uniqueId);
        if (gamePlayer == null) {
            throw new NullPointerException();
        }
        return gamePlayer;
    }

    public GamePlayer getPlayer(int playerId) {
        GamePlayer gamePlayer = byPlayerId.get(playerId);
        if (gamePlayer == null) {
            throw new NullPointerException();
        }
        return gamePlayer;
    }

    public Collection<GamePlayer> getPlayers() {
        return Collections.unmodifiableCollection(byUniqueId.values());
    }

    public void removePlayer(GamePlayer player) {
        byUniqueId.remove(player.getUniqueId());
        byPlayerId.remove(player.getPlayerId());
        if (log.isDebugEnabled()) {
            log.debug("Remove GamePlayer: {}", player.getName());
        }
    }
}
