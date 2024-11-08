package top.nipuru.minegame.game.task

import top.nipuru.minegame.game.BukkitPlugin
import top.nipuru.minegame.game.playerList

/**
 * 主线程调度 每 1-tick 执行一次
 */

class ServerTickTask(private val plugin: BukkitPlugin) : Runnable {
    override fun run() {
        playerList.tick()
    }
}
