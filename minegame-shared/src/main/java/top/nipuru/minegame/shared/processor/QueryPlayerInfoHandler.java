package top.nipuru.minegame.shared.processor;

import lombok.AllArgsConstructor;
import top.nipuru.minegame.common.message.shared.PlayerInfoMessage;
import top.nipuru.minegame.common.message.shared.QueryPlayerInfoRequest;
import top.nipuru.minegame.common.processor.RequestDispatcher;
import top.nipuru.minegame.shared.SharedServer;

@AllArgsConstructor
public class QueryPlayerInfoHandler implements RequestDispatcher.Handler<QueryPlayerInfoRequest> {

    private final SharedServer server;

    @Override
    public void handle(RequestDispatcher.ResponseSender responseSender, QueryPlayerInfoRequest request) throws Exception {
        PlayerInfoMessage playerInfo = server.getPlayerInfoManager().getByName(request.getName());
        responseSender.sendResponse(playerInfo);
    }

    @Override
    public Class<QueryPlayerInfoRequest> interest() {
        return null;
    }
}
