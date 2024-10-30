package top.nipuru.minegame.server.listener;

import lombok.extern.slf4j.Slf4j;
import top.nipuru.minegame.server.BukkitPlugin;
import top.nipuru.minegame.server.TimeMgr;
import top.nipuru.minegame.server.constants.TimeSecs;
import top.nipuru.minegame.server.game.core.CoreManager;
import top.nipuru.minegame.server.game.player.GamePlayer;
import top.nipuru.minegame.server.game.player.DataInfo;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Map;
import java.util.UUID;

@Slf4j
public class PlayerJoinListener implements Listener {

    private final BukkitPlugin plugin;
    private final Map<UUID, DataInfo> pendingPlayers;

    public PlayerJoinListener(BukkitPlugin plugin, Map<UUID, DataInfo> pendingPlayers) {
        this.plugin = plugin;
        this.pendingPlayers = pendingPlayers;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEventLowest(PlayerJoinEvent event) {
        event.joinMessage(null);

        Player player = event.getPlayer();
        try {
            handleJoin(player);
        } catch (Exception e) {
            TextComponent message = Component.text("加入游戏失败！请重试").color(NamedTextColor.RED);
            player.kick(message);
            log.error(e.getMessage(), e);
        }
    }

    private void handleJoin(Player player) throws Exception {
        // PlayerDataInfo 在 PreLogin 的时候就应该加载好了
        DataInfo dataInfo = pendingPlayers.remove(player.getUniqueId());
        if (dataInfo == null) {
            throw new NullPointerException("Player " + player.getName() + " has no pending data info");
        }

        GamePlayer gamePlayer = new GamePlayer(plugin, dataInfo.getPlayerId(), dataInfo.getDbId(), player);
        gamePlayer.unpack(dataInfo);
        plugin.getPlayerList().registerPlayer(gamePlayer);
        gamePlayer.init();

        // 玩家登录
        if (!gamePlayer.getCoreManager().isOnline()) onLogin(gamePlayer);

        // 玩家加入服务器
        gamePlayer.onJoin();
    }

    private void onLogin(GamePlayer gamePlayer) {
        CoreManager coreManager = gamePlayer.getCoreManager();
        coreManager.setOnline(true);
        if (coreManager.getPlayerData().resetTime() < TimeMgr.dayZero()) {
            long lastResetTime = coreManager.getPlayerData().resetTime();
            // 代表是新玩家
            if (lastResetTime == 0L) {
                gamePlayer.initNew();
                gamePlayer.onNewDay(TimeMgr.dayZero());
            } else {
                // 最多重置30天
                if (TimeMgr.dayZero() - lastResetTime > 30 * TimeSecs.DAY) {
                    lastResetTime = TimeMgr.dayZero() - 30 * TimeSecs.DAY;
                }
                while (lastResetTime + TimeSecs.DAY <= TimeMgr.dayZero()) {
                    lastResetTime += TimeSecs.DAY;
                    gamePlayer.onNewDay(lastResetTime);
                }
            }
        }
        gamePlayer.onLogin();
    }
}
