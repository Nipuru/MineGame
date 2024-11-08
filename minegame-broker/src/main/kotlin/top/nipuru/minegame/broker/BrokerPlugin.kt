package top.nipuru.minegame.broker

import net.afyer.afybroker.server.plugin.Plugin
import org.slf4j.LoggerFactory
import top.nipuru.minegame.broker.game.GamePlayerList
import top.nipuru.minegame.broker.processor.*

val logger = LoggerFactory.getLogger("MineGame")

val playerList = GamePlayerList()

class BrokerPlugin : Plugin() {
    override fun onEnable() {
        server.registerUserProcessor(RequestMessageRouter())
        server.registerUserProcessor(PlayerDataTransferBrokerProcessor())
        server.registerUserProcessor(PlayerOfflineDataBrokerProcessor())
        server.registerUserProcessor(PlayerChatBrokerProcessor())
        server.registerUserProcessor(PlayerPrivateChatBrokerProcessor())
        server.registerUserProcessor(PlayerRegisterBrokerProcessor())
    }
}
