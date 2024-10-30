package top.nipuru.minegame.broker.processor;

import top.nipuru.minegame.broker.BrokerPlugin;
import top.nipuru.minegame.broker.game.GamePlayer;
import top.nipuru.minegame.common.message.PlayerRegisterMessage;
import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.AsyncUserProcessor;
import net.afyer.afybroker.server.proxy.BrokerPlayer;

public class PlayerRegisterBrokerProcessor extends AsyncUserProcessor<PlayerRegisterMessage> {

    private final BrokerPlugin plugin;

    public PlayerRegisterBrokerProcessor(BrokerPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handleRequest(BizContext bizContext, AsyncContext asyncContext, PlayerRegisterMessage request) {
        BrokerPlayer brokerPlayer = plugin.getServer().getPlayer(request.getUniqueId());
        if (brokerPlayer == null) return;
        GamePlayer player = new GamePlayer(request.getPlayerId(), brokerPlayer);
        plugin.getPlayerList().registerPlayer(player);
    }

    @Override
    public String interest() {
        return PlayerRegisterMessage.class.getName();
    }
}
