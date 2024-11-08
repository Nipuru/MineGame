package top.nipuru.minegame.shared.processor

import com.alipay.remoting.AsyncContext
import top.nipuru.minegame.common.message.shared.PlayerInfoUpdateNotify
import top.nipuru.minegame.common.processor.RequestDispatcher
import top.nipuru.minegame.shared.player.PlayerInfoManager

class PlayerInfoUpdateHandler : RequestDispatcher.Handler<PlayerInfoUpdateNotify> {

    override fun handle(asyncCtx: AsyncContext, request: PlayerInfoUpdateNotify) {
        PlayerInfoManager.insertOrUpdate(request.playerInfo)
    }

    override fun interest(): Class<PlayerInfoUpdateNotify> {
        return PlayerInfoUpdateNotify::class.java
    }
}
