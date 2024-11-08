package top.nipuru.minegame.broker.processor

import com.alipay.remoting.BizContext
import com.alipay.remoting.rpc.protocol.SyncUserProcessor
import net.afyer.afybroker.core.BrokerClientType
import net.afyer.afybroker.server.Broker
import top.nipuru.minegame.broker.game.GamePlayer
import top.nipuru.minegame.broker.playerList
import top.nipuru.minegame.common.ClientTag
import top.nipuru.minegame.common.message.PlayerChatMessage

class PlayerChatBrokerProcessor : SyncUserProcessor<PlayerChatMessage>() {

    override fun handleRequest(bizContext: BizContext, request: PlayerChatMessage): Any {
        val player: GamePlayer = playerList.getPlayer(request.sender.playerId)
        val chatLimiter = player.chatLimiter
        if (chatLimiter.isLimit) {
            return PlayerChatMessage.RATE_LIMIT
        }
        for (client in Broker.getClientManager().list()) {
            if (client.type != BrokerClientType.SERVER) continue
            if (!client.hasTag(ClientTag.GAME)) continue
            client.oneway(request)
        }
        return PlayerChatMessage.SUCCESS
    }

    override fun interest(): String {
        return PlayerChatMessage::class.java.name
    }
}
