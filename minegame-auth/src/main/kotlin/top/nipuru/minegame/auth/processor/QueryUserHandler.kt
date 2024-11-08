package top.nipuru.minegame.auth.processor

import com.alipay.remoting.AsyncContext
import top.nipuru.minegame.auth.user.UserManager
import top.nipuru.minegame.common.message.auth.QueryUserRequest
import top.nipuru.minegame.common.message.auth.UserMessage
import top.nipuru.minegame.common.processor.RequestDispatcher

class QueryUserHandler : RequestDispatcher.Handler<QueryUserRequest> {
    override fun handle(asyncCtx: AsyncContext, request: QueryUserRequest) {
        val user = UserManager.initUser(request.uniqueId, request.ip)
        val userMessage: UserMessage = UserMessage(user.playerId, user.dbId)
        asyncCtx.sendResponse(userMessage)
    }

    override fun interest(): Class<QueryUserRequest> {
        return QueryUserRequest::class.java
    }
}
