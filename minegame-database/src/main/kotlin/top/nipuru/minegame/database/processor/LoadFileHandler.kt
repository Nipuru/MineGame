package top.nipuru.minegame.database.processor

import com.alipay.remoting.AsyncContext
import top.nipuru.minegame.common.message.database.LoadFileRequest
import top.nipuru.minegame.common.processor.RequestDispatcher
import top.nipuru.minegame.database.file.FileManager

class LoadFileHandler : RequestDispatcher.Handler<LoadFileRequest> {

    override fun handle(asyncCtx: AsyncContext, request: LoadFileRequest) {
        val data: ByteArray = FileManager.getFile(request.filename)
        asyncCtx.sendResponse(data)
    }

    override fun interest(): Class<LoadFileRequest> {
        return LoadFileRequest::class.java
    }
}
