package top.nipuru.minegame.broker.processor;

import top.nipuru.minegame.broker.BrokerPlugin;
import top.nipuru.minegame.broker.game.GamePlayer;
import top.nipuru.minegame.broker.util.LeakBucketLimiter;
import top.nipuru.minegame.common.message.PlayerPrivateChatMessage;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.SyncUserProcessor;
import net.afyer.afybroker.server.Broker;
import net.afyer.afybroker.server.proxy.BrokerPlayer;

public class PlayerPrivateChatBrokerProcessor extends SyncUserProcessor<PlayerPrivateChatMessage> {

    private final BrokerPlugin plugin;

    public PlayerPrivateChatBrokerProcessor(BrokerPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Object handleRequest(BizContext bizContext, PlayerPrivateChatMessage request) throws Exception {
        GamePlayer player = plugin.getPlayer(request.getSender().getPlayerId());
        if (player == null) {
            return PlayerPrivateChatMessage.FAILURE;
        }
        LeakBucketLimiter chatLimiter = player.getChatLimiter();
        if (chatLimiter.isLimit()) {
            return PlayerPrivateChatMessage.RATE_LIMIT;
        }
        BrokerPlayer receiver = Broker.getPlayer(request.getReceiver());
        if (receiver == null || receiver.getServer() == null) {
            return PlayerPrivateChatMessage.NOT_ONLINE;
        }
        return receiver.getServer().invokeSync(request);
    }

    @Override
    public String interest() {
        return PlayerPrivateChatMessage.class.getName();
    }
}
