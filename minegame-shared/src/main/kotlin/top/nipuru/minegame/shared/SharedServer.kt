package top.nipuru.minegame.shared

import com.alipay.remoting.ConnectionEventType
import com.alipay.remoting.LifeCycleException
import net.afyer.afybroker.client.Broker
import net.afyer.afybroker.client.BrokerClient
import net.afyer.afybroker.client.BrokerClientBuilder
import net.afyer.afybroker.core.util.BoltUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import top.nipuru.minegame.common.ClientType
import top.nipuru.minegame.common.processor.RequestDispatcher
import top.nipuru.minegame.database.config.Config
import top.nipuru.minegame.database.config.loadConfig
import top.nipuru.minegame.shared.SharedServer.dataSourceProvider
import top.nipuru.minegame.shared.SharedServer.shutdown
import top.nipuru.minegame.shared.SharedServer.startup
import top.nipuru.minegame.shared.auction.ListingManager
import top.nipuru.minegame.shared.datasource.DataSourceProvider
import top.nipuru.minegame.shared.datasource.HikariPgSQLProvider
import top.nipuru.minegame.shared.player.PlayerInfoManager
import top.nipuru.minegame.shared.processor.PlayerInfoUpdateHandler
import top.nipuru.minegame.shared.processor.connection.CloseEventSharedProcessor
import javax.sql.DataSource

val logger: Logger = LoggerFactory.getLogger("MineGame")
val dataSource: DataSource
    get() = dataSourceProvider.dataSource

fun main() {
    startup()
    Runtime.getRuntime().addShutdownHook(Thread { shutdown() })
}

internal object SharedServer {
    private lateinit var brokerClient: BrokerClient
    lateinit var dataSourceProvider: DataSourceProvider

    fun startup() {
        val config = loadConfig()

        initDataSource(config)
        initBrokerClient(config)

        ListingManager.init()
        PlayerInfoManager.init()
    }

    fun shutdown() {
        brokerClient.shutdown()
        dataSourceProvider.shutdown()
    }

    private fun buildBrokerClient(builder: BrokerClientBuilder) {
        val dispatcher = RequestDispatcher()
        dispatcher.registerHandler(PlayerInfoUpdateHandler())
        dispatcher.registerHandler(PlayerInfoUpdateHandler())

        builder.registerUserProcessor(dispatcher)
        builder.addConnectionEventProcessor(ConnectionEventType.CLOSE, CloseEventSharedProcessor())
    }

    private fun initDataSource(config: Config) {
        dataSourceProvider = HikariPgSQLProvider()
        val datasource = config.datasource
        dataSourceProvider.init(
            datasource.host, datasource.port,
            datasource.database, datasource.username, datasource.password
        )
    }

    private fun initBrokerClient(config: Config) {
        try {
            val builder = BrokerClient.newBuilder()
            builder.host(config.broker.host)
            builder.port(config.broker.port)
            builder.name(ClientType.SHARED)
            builder.type(ClientType.SHARED)
            this.buildBrokerClient(builder)

            brokerClient = builder.build()
            Broker.setClient(brokerClient)
            BoltUtils.initProtocols()
            brokerClient.startup()
            brokerClient.ping()
        } catch (e: LifeCycleException) {
            logger.error("Broker client startup failed!")
            throw e
        } catch (e: Exception) {
            logger.error("Ping to the broker server failed!")
            throw e
        }
    }
}
