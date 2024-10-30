package top.nipuru.minegame.database.processor;

import lombok.AllArgsConstructor;
import top.nipuru.minegame.common.message.database.PlayerTransactionRequest;
import top.nipuru.minegame.common.processor.RequestDispatcher;
import top.nipuru.minegame.database.DatabaseServer;

@AllArgsConstructor
public class PlayerTransactionHandler implements RequestDispatcher.Handler<PlayerTransactionRequest> {

    private final DatabaseServer server;

    @Override
    public void handle(RequestDispatcher.ResponseSender responseSender, PlayerTransactionRequest request) throws Exception {
        server.getPlayerDataManager().transaction(request);
        responseSender.sendResponse(true); // response
    }

    @Override
    public Class<PlayerTransactionRequest> interest() {
        return PlayerTransactionRequest.class;
    }
}
