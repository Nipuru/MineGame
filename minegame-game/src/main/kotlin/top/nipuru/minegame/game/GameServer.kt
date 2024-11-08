package top.nipuru.minegame.game

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.bukkit.plugin.Plugin
import org.slf4j.LoggerFactory
import top.nipuru.minegame.game.gameplay.player.GamePlayerList
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executor


/**
 * @author Nipuru
 * @since 2024/11/08 13:04
 */
val logger = LoggerFactory.getLogger("GameServer")

val playerList = GamePlayerList()

val enableLatch = CountDownLatch(1)

val gson: Gson = GsonBuilder().create()

lateinit var bizThread: Executor
    internal set

lateinit var plugin: Plugin
    internal set


