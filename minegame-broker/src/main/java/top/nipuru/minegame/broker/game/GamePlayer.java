package top.nipuru.minegame.broker.game;

import top.nipuru.minegame.broker.util.LeakBucketLimiter;
import lombok.Getter;
import net.afyer.afybroker.server.proxy.BrokerPlayer;

import java.util.UUID;

@Getter
public class GamePlayer {

    private final int playerId;
    private final BrokerPlayer brokerPlayer;
    // 每秒允许 0.25条消息，最多积累3条消息
    private final LeakBucketLimiter chatLimiter = new LeakBucketLimiter(0.25, 3);

    public GamePlayer(int playerId, BrokerPlayer brokerPlayer) {
        this.playerId = playerId;
        this.brokerPlayer = brokerPlayer;
    }

    public String getName() {
        return brokerPlayer.getName();
    }

    public UUID getUniqueId() {
        return brokerPlayer.getUniqueId();
    }


}
