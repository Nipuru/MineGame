package top.nipuru.minegame.broker.processor;

import top.nipuru.minegame.broker.BrokerPlugin;
import top.nipuru.minegame.broker.game.GamePlayer;
import top.nipuru.minegame.common.ClientTag;
import top.nipuru.minegame.common.message.PlayerOfflineDataMessage;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.SyncUserProcessor;
import lombok.extern.slf4j.Slf4j;
import net.afyer.afybroker.server.proxy.BrokerClientItem;

@Slf4j
public class PlayerOfflineDataBrokerProcessor extends SyncUserProcessor<PlayerOfflineDataMessage> {

    private final BrokerPlugin plugin;

    public PlayerOfflineDataBrokerProcessor(BrokerPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Object handleRequest(BizContext bizContext, PlayerOfflineDataMessage request) throws Exception {
        // 如果在线处理了则不需要新增离线消息
        if (onlineRequest(request)) return true;

        // 新增离线消息
        return offlineRequest(request);
    }

    private boolean onlineRequest(PlayerOfflineDataMessage request) throws Exception {
        GamePlayer player = plugin.getPlayer(request.getPlayerId());

        BrokerClientItem bukkit = player.getBrokerPlayer().getServer();
        if (bukkit == null) return false;
        if (!bukkit.hasTag(ClientTag.GAME)) return false;

        return bukkit.invokeSync(request);
    }

    private boolean offlineRequest(PlayerOfflineDataMessage request) throws Exception {
        BrokerClientItem dbServer = plugin.getDbServer(request.getDbId());
        if (dbServer == null) return false;

        return dbServer.invokeSync(request);
    }

    @Override
    public String interest() {
        return PlayerOfflineDataMessage.class.getName();
    }
}
