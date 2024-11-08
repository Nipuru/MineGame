package top.nipuru.minegame.broker.processor;

import top.nipuru.minegame.common.ClientTag;
import top.nipuru.minegame.common.message.PlayerDataTransferRequest;
import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.AsyncUserProcessor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.afyer.afybroker.core.util.AbstractInvokeCallback;
import net.afyer.afybroker.server.BrokerServer;
import net.afyer.afybroker.server.aware.BrokerServerAware;
import net.afyer.afybroker.server.proxy.BrokerClientItem;
import net.afyer.afybroker.server.proxy.BrokerPlayer;

@Setter
@Slf4j
public class PlayerDataTransferBrokerProcessor extends AsyncUserProcessor<PlayerDataTransferRequest> implements BrokerServerAware {

    private BrokerServer brokerServer;

    @Override
    public void handleRequest(BizContext bizCtx, AsyncContext asyncCtx, PlayerDataTransferRequest request) throws Exception {
        BrokerPlayer player = brokerServer.getPlayer(request.getUniqueId());
        if (player == null) {
            asyncCtx.sendResponse(null);
            return;
        }
        BrokerClientItem currentServer = player.getServer();
        if (currentServer == null || !currentServer.hasTag(ClientTag.GAME)) {
            asyncCtx.sendResponse(null);
            return;
        }

        // 收到消息后将消息转发给玩家所在的bukkit服务器
        currentServer.invokeWithCallback(request, new AbstractInvokeCallback() {
            @Override
            public void onResponse(Object result) {
                asyncCtx.sendResponse(result);
            }

            @Override
            public void onException(Throwable e) {
                log.error(e.getMessage(), e);
                asyncCtx.sendException(e);
            }
        });
    }

    @Override
    public String interest() {
        return PlayerDataTransferRequest.class.getName();
    }
}
