package top.nipuru.minegame.server.task;

import lombok.extern.slf4j.Slf4j;
import top.nipuru.minegame.server.BukkitPlugin;

/**
 * 主线程调度 每 1-tick 执行一次
 */
@Slf4j
public class ServerTickTask implements Runnable {

    private final BukkitPlugin plugin;

    public ServerTickTask(BukkitPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        this.tick();
    }

    private void tick() {
        plugin.getPlayerList().tick();
    }

}
