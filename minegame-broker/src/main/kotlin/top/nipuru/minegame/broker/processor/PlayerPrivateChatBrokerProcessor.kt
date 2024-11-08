package top.nipuru.minegame.broker.processor

import com.alipay.remoting.BizContext
import com.alipay.remoting.rpc.protocol.SyncUserProcessor
import net.afyer.afybroker.server.Broker
import top.nipuru.minegame.broker.playerList
import top.nipuru.minegame.common.message.PlayerPrivateChatMessage

class PlayerPrivateChatBrokerProcessor : SyncUserProcessor<PlayerPrivateChatMessage>() {

    override fun handleRequest(bizContext: BizContext, request: PlayerPrivateChatMessage): Any {
        val player = playerList.getPlayer(request.sender.playerId)
        val chatLimiter = player.chatLimiter
        if (chatLimiter.isLimit) {
            return PlayerPrivateChatMessage.RATE_LIMIT
        }
        val receiver = Broker.getPlayer(request.receiver)
        if (receiver == null || receiver.server == null) {
            return PlayerPrivateChatMessage.NOT_ONLINE
        }
        return receiver.server!!.invokeSync(request)
    }

    override fun interest(): String {
        return PlayerPrivateChatMessage::class.java.name
    }
}
