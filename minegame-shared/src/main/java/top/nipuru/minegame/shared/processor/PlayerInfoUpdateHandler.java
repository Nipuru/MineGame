package top.nipuru.minegame.shared.processor;

import lombok.AllArgsConstructor;
import top.nipuru.minegame.common.message.shared.PlayerInfoUpdateNotify;
import top.nipuru.minegame.common.processor.RequestDispatcher;
import top.nipuru.minegame.shared.SharedServer;

@AllArgsConstructor
public class PlayerInfoUpdateHandler implements RequestDispatcher.Handler<PlayerInfoUpdateNotify> {

    private final SharedServer server;

    @Override
    public void handle(RequestDispatcher.ResponseSender responseSender, PlayerInfoUpdateNotify request) throws Exception {
        server.getPlayerInfoManager().insertOrUpdate(request.getPlayerInfo());
    }

    @Override
    public Class<PlayerInfoUpdateNotify> interest() {
        return PlayerInfoUpdateNotify.class;
    }
}
