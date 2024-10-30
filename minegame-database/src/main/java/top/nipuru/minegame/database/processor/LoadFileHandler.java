package top.nipuru.minegame.database.processor;

import lombok.AllArgsConstructor;
import top.nipuru.minegame.common.message.database.LoadFileRequest;
import top.nipuru.minegame.common.processor.RequestDispatcher;
import top.nipuru.minegame.database.DatabaseServer;

@AllArgsConstructor
public class LoadFileHandler implements RequestDispatcher.Handler<LoadFileRequest> {

    private final DatabaseServer server;

    @Override
    public void handle(RequestDispatcher.ResponseSender responseSender, LoadFileRequest request) throws Exception {
        byte[] data = server.getFileManager().getFile(request.getFilename());
        responseSender.sendResponse(data);
    }

    @Override
    public Class<LoadFileRequest> interest() {
        return LoadFileRequest.class;
    }
}
