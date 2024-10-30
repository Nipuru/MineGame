package top.nipuru.minegame.broker.processor;

import top.nipuru.minegame.broker.BrokerPlugin;
import top.nipuru.minegame.broker.game.GamePlayer;
import top.nipuru.minegame.broker.util.LeakBucketLimiter;
import top.nipuru.minegame.common.ClientTag;
import top.nipuru.minegame.common.ClientType;
import top.nipuru.minegame.common.message.PlayerChatMessage;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.SyncUserProcessor;
import net.afyer.afybroker.server.proxy.BrokerClientItem;

public class PlayerChatBrokerProcessor extends SyncUserProcessor<PlayerChatMessage> {

    private final BrokerPlugin plugin;

    public PlayerChatBrokerProcessor(BrokerPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Object handleRequest(BizContext bizContext, PlayerChatMessage request) throws Exception {
        GamePlayer player = plugin.getPlayer(request.getSender().getPlayerId());
        if (player == null) {
            return PlayerChatMessage.FAILURE;
        }
        LeakBucketLimiter chatLimiter = player.getChatLimiter();
        if (chatLimiter.isLimit()) {
            return PlayerChatMessage.RATE_LIMIT;
        }
        for (BrokerClientItem client : plugin.getServer().getClientManager().list()) {
            if (!client.getType().equals(ClientType.SERVER)) continue;
            if (!client.hasTag(ClientTag.GAME)) continue;
            client.oneway(request);
        }
        return PlayerChatMessage.SUCCESS;
    }

    @Override
    public String interest() {
        return PlayerChatMessage.class.getName();
    }
}
