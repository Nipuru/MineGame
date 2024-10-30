package top.nipuru.minegame.broker.processor;

import top.nipuru.minegame.broker.BrokerPlugin;
import top.nipuru.minegame.common.ClientType;
import top.nipuru.minegame.common.RequestMessageContainer;
import top.nipuru.minegame.common.message.AuthServerRequest;
import top.nipuru.minegame.common.message.DatabaseServerRequest;
import top.nipuru.minegame.common.message.SharedServerRequest;
import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.AsyncMultiInterestUserProcessor;
import net.afyer.afybroker.core.util.AbstractInvokeCallback;
import net.afyer.afybroker.core.util.BoltUtils;
import net.afyer.afybroker.server.proxy.BrokerClientItem;

import java.util.List;

public class RequestMessageRouter extends AsyncMultiInterestUserProcessor<RequestMessageContainer> {

    private final BrokerPlugin plugin;

    public RequestMessageRouter(BrokerPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handleRequest(BizContext bizCtx, AsyncContext asyncCtx, RequestMessageContainer request) throws Exception {
        BrokerClientItem clientProxy = route(request);
        if (clientProxy == null) {
            throw new Exception("No server available for request: " + request);
        }

        if (BoltUtils.hasResponse(bizCtx)) {
            clientProxy.invokeWithCallback(request.getRequestMessage(), new AbstractInvokeCallback() {
                @Override
                public void onResponse(Object result) {
                    asyncCtx.sendResponse(result);
                }

                @Override
                public void onException(Throwable e) {
                    asyncCtx.sendException(e);
                }
            }, bizCtx.getClientTimeout());
        } else {
            clientProxy.oneway(request.getRequestMessage());
        }
    }

    @Override
    public List<String> multiInterest() {
        return List.of(AuthServerRequest.class.getName(), DatabaseServerRequest.class.getName(), SharedServerRequest.class.getName());
    }

    private BrokerClientItem route(RequestMessageContainer message) {
        if (message instanceof AuthServerRequest) {
            String name = ClientType.AUTH;
            return plugin.getServer().getClient(name);
        } else if (message instanceof DatabaseServerRequest databaseServerRequest) {
            return plugin.getDbServer(databaseServerRequest.getDbId());
        } else if (message instanceof SharedServerRequest) {
            String name = ClientType.SHARED;
            return plugin.getServer().getClient(name);
        }
        return null;
    }
}
