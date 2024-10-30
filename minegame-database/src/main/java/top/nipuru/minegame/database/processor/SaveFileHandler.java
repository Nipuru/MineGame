package top.nipuru.minegame.database.processor;

import lombok.AllArgsConstructor;
import top.nipuru.minegame.common.message.database.SaveFileRequest;
import top.nipuru.minegame.common.processor.RequestDispatcher;
import top.nipuru.minegame.database.DatabaseServer;

@AllArgsConstructor
public class SaveFileHandler implements RequestDispatcher.Handler<SaveFileRequest> {

    private final DatabaseServer server;

    @Override
    public void handle(RequestDispatcher.ResponseSender responseSender, SaveFileRequest request) throws Exception {
        server.getFileManager().saveFile(request.getFilename(), request.getData());
        responseSender.sendResponse(true); // response
    }

    @Override
    public Class<SaveFileRequest> interest() {
        return SaveFileRequest.class;
    }
}
