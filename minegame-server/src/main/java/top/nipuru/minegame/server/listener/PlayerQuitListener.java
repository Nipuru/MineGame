package top.nipuru.minegame.server.listener;

import top.nipuru.minegame.server.BukkitPlugin;
import top.nipuru.minegame.server.game.player.GamePlayer;
import top.nipuru.minegame.server.util.NmsMethods;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    private final BukkitPlugin plugin;

    public PlayerQuitListener(BukkitPlugin plugin) {
        this.plugin = plugin;
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onEventMonitor(PlayerQuitEvent event) {
        event.quitMessage(null);
        Player player = event.getPlayer();
        GamePlayer gamePlayer = plugin.getPlayer(player.getUniqueId());
        if (gamePlayer == null) return;
        // 由于存在跨服机制 所以 Quit 可能被触发多次 但只有第一次有效
        if (player.isOnline()) handleQuit(player, gamePlayer);
    }

    private void handleQuit(Player player, GamePlayer gamePlayer) {
        // 玩家退出方法
        gamePlayer.onQuit();

        // 判断玩家是否断开连接（跨服不会断开连接 因为是被强制移出玩家列表 并没有断连）
        if (NmsMethods.hasDisconnected(player)) {
            gamePlayer.getCoreManager().setOnline(false);
            gamePlayer.onLogout();
        }

        // 最后再调用一次 tick 方法
        gamePlayer.tick(System.currentTimeMillis());

        plugin.getPlayerList().removePlayer(gamePlayer);
    }

}
