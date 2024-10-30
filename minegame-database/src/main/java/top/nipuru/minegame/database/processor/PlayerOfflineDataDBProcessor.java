package top.nipuru.minegame.database.processor;

import lombok.AllArgsConstructor;
import top.nipuru.minegame.common.message.PlayerOfflineDataMessage;
import top.nipuru.minegame.database.DatabaseServer;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.SyncUserProcessor;

@AllArgsConstructor
public class PlayerOfflineDataDBProcessor extends SyncUserProcessor<PlayerOfflineDataMessage> {

    private final DatabaseServer server;

    @Override
    public Object handleRequest(BizContext bizContext, PlayerOfflineDataMessage request) throws Exception {
        server.getOfflineDataManager().insert(request);
        return true;
    }

    @Override
    public String interest() {
        return PlayerOfflineDataMessage.class.getName();
    }
}
