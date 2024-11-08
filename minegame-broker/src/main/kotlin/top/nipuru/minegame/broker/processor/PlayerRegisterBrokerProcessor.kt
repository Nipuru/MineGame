package top.nipuru.minegame.broker.processor

import com.alipay.remoting.AsyncContext
import com.alipay.remoting.BizContext
import com.alipay.remoting.rpc.protocol.AsyncUserProcessor
import net.afyer.afybroker.server.Broker
import net.afyer.afybroker.server.proxy.BrokerPlayer
import top.nipuru.minegame.broker.game.GamePlayer
import top.nipuru.minegame.broker.playerList
import top.nipuru.minegame.common.message.PlayerRegisterMessage

class PlayerRegisterBrokerProcessor : AsyncUserProcessor<PlayerRegisterMessage>() {
    override fun handleRequest(bizContext: BizContext, asyncContext: AsyncContext, request: PlayerRegisterMessage) {
        val brokerPlayer = Broker.getPlayer(request.uniqueId) ?: return
        val player = GamePlayer(request.playerId, brokerPlayer)
        playerList.registerPlayer(player)
    }

    override fun interest(): String {
        return PlayerRegisterMessage::class.java.name
    }
}
