package top.nipuru.minegame.database.processor

import com.alipay.remoting.AsyncContext
import top.nipuru.minegame.common.message.database.PlayerTransactionRequest
import top.nipuru.minegame.common.processor.RequestDispatcher
import top.nipuru.minegame.database.player.PlayerDataManager

class PlayerTransactionHandler : RequestDispatcher.Handler<PlayerTransactionRequest> {

    override fun handle(asyncCtx: AsyncContext, request: PlayerTransactionRequest) {
        PlayerDataManager.transaction(request)
        asyncCtx.sendResponse(true) // response
    }

    override fun interest(): Class<PlayerTransactionRequest> {
        return PlayerTransactionRequest::class.java
    }
}
