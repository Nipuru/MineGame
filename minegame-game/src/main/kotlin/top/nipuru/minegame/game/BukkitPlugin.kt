package top.nipuru.minegame.game

import com.google.common.util.concurrent.ThreadFactoryBuilder
import net.afyer.afybroker.client.Broker
import net.afyer.afybroker.client.BrokerClientBuilder
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import top.nipuru.minegame.common.ClientTag
import top.nipuru.minegame.common.processor.RequestDispatcher
import top.nipuru.minegame.game.gameplay.player.GamePlayer
import top.nipuru.minegame.game.listener.AsyncPlayerPreLoginListener
import top.nipuru.minegame.game.listener.PlayerChatListener
import top.nipuru.minegame.game.listener.PlayerJoinListener
import top.nipuru.minegame.game.listener.PlayerQuitListener
import top.nipuru.minegame.game.processor.PlayerChatServerProcessor
import top.nipuru.minegame.game.processor.PlayerDataTransferBukkitProcessor
import top.nipuru.minegame.game.processor.PlayerOfflineDataBukkitProcessor
import top.nipuru.minegame.game.processor.PlayerPrivateChatServerProcessor
import top.nipuru.minegame.game.task.ServerTickTask
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * @author Nipuru
 * @since 2024/9/16 0:17
 */
class BukkitPlugin : JavaPlugin() {

    private val bizExecutorService: ExecutorService = Executors.newCachedThreadPool(
        ThreadFactoryBuilder()
            .setDaemon(false)
            .setNameFormat("MineGame-bizThread-%d")
            .build()
    )

    override fun onLoad() {
        plugin = this
        bizThread = bizExecutorService
        Broker.buildAction(this::buildBrokerClient)
    }

    private fun buildBrokerClient(builder: BrokerClientBuilder) {
        val dispatcher = RequestDispatcher()

        builder.addTag(ClientTag.GAME)
        builder.registerUserProcessor(dispatcher)
        builder.registerUserProcessor(PlayerDataTransferBukkitProcessor())
        builder.registerUserProcessor(PlayerOfflineDataBukkitProcessor())
        builder.registerUserProcessor(PlayerChatServerProcessor())
        builder.registerUserProcessor(PlayerPrivateChatServerProcessor())
    }

    override fun onEnable() {
        Bukkit.getScheduler().runTaskTimer(this, ServerTickTask(this), 1L, 1L)

        val pendingPlayers: MutableMap<UUID, GamePlayer> = HashMap()
        Bukkit.getPluginManager().registerEvents(AsyncPlayerPreLoginListener(pendingPlayers), this)
        Bukkit.getPluginManager().registerEvents(PlayerJoinListener(pendingPlayers), this)
        Bukkit.getPluginManager().registerEvents(PlayerQuitListener(), this)
        Bukkit.getPluginManager().registerEvents(PlayerChatListener(), this)

        enableLatch.countDown()
    }

    override fun onDisable() {
        bizExecutorService.shutdown()
        bizExecutorService.awaitTermination(1L, TimeUnit.MINUTES)
    }
}
