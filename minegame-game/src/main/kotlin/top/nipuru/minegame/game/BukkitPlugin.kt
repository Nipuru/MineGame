package top.nipuru.minegame.game

import com.google.common.util.concurrent.ThreadFactoryBuilder
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.afyer.afybroker.client.Broker
import net.afyer.afybroker.client.BrokerClientBuilder
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import org.slf4j.LoggerFactory
import top.nipuru.minegame.common.ClientTag
import top.nipuru.minegame.common.processor.RequestDispatcher
import top.nipuru.minegame.game.gameplay.player.GamePlayer
import top.nipuru.minegame.game.gameplay.player.GamePlayerList
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
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * @author Nipuru
 * @since 2024/9/16 0:17
 */
val logger = LoggerFactory.getLogger("MineGame")

val playerList = GamePlayerList()

val enableLatch = CountDownLatch(1)

val gson: Gson = GsonBuilder().create()

lateinit var bizThread: ExecutorService
    private set

lateinit var plugin: Plugin
    private set

class BukkitPlugin : JavaPlugin() {

    override fun onLoad() {
        plugin = this
        bizThread = Executors.newCachedThreadPool(
            ThreadFactoryBuilder()
                .setDaemon(false)
                .setNameFormat("MineGame-bizThread-%d")
                .build()
        )
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
        bizThread.shutdown()
        bizThread.awaitTermination(1L, TimeUnit.MINUTES)
    }
}
