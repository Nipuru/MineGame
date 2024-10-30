package top.nipuru.minegame.database.processor;

import top.nipuru.minegame.common.message.database.FieldMessage;
import top.nipuru.minegame.common.message.database.QueryPlayerRequest;
import top.nipuru.minegame.common.processor.RequestDispatcher;
import top.nipuru.minegame.database.DatabaseServer;

import java.util.*;

public class QueryPlayerHandler implements RequestDispatcher.Handler<QueryPlayerRequest> {

    private final DatabaseServer server;


    public QueryPlayerHandler(DatabaseServer server) {
        this.server = server;
    }

    @Override
    public void handle(RequestDispatcher.ResponseSender responseSender, QueryPlayerRequest request) throws Exception {
        Map<String, List<List<FieldMessage>>> data = server.getPlayerDataManager().queryPlayer(request);
        responseSender.sendResponse(data);
    }

    @Override
    public Class<QueryPlayerRequest> interest() {
        return QueryPlayerRequest.class;
    }
}
