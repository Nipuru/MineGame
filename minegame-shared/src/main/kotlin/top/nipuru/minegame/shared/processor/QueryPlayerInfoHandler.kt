package top.nipuru.minegame.shared.processor

import com.alipay.remoting.AsyncContext
import top.nipuru.minegame.common.message.shared.PlayerInfoMessage
import top.nipuru.minegame.common.message.shared.QueryPlayerInfoRequest
import top.nipuru.minegame.common.processor.RequestDispatcher
import top.nipuru.minegame.shared.player.PlayerInfoManager

class QueryPlayerInfoHandler : RequestDispatcher.Handler<QueryPlayerInfoRequest> {

    override fun handle(asyncCtx: AsyncContext, request: QueryPlayerInfoRequest) {
        val playerInfo: PlayerInfoMessage? = PlayerInfoManager.getByName(request.name)
        asyncCtx.sendResponse(playerInfo)
    }

    override fun interest(): Class<QueryPlayerInfoRequest> {
        return QueryPlayerInfoRequest::class.java
    }
}
