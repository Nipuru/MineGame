package top.nipuru.minegame.database.processor

import com.alipay.remoting.AsyncContext
import top.nipuru.minegame.common.message.database.SaveFileRequest
import top.nipuru.minegame.common.processor.RequestDispatcher
import top.nipuru.minegame.database.file.FileManager

class SaveFileHandler : RequestDispatcher.Handler<SaveFileRequest> {

    override fun handle(asyncCtx: AsyncContext, request: SaveFileRequest) {
        FileManager.saveFile(request.filename, request.data)
        asyncCtx.sendResponse(true) // response
    }

    override fun interest(): Class<SaveFileRequest> {
        return SaveFileRequest::class.java
    }
}
