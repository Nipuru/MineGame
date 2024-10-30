package top.nipuru.minegame.server.listener;

import lombok.extern.slf4j.Slf4j;
import top.nipuru.minegame.common.message.PlayerDataTransferRequest;
import top.nipuru.minegame.common.message.PlayerRegisterMessage;
import top.nipuru.minegame.common.message.auth.QueryUserRequest;
import top.nipuru.minegame.common.message.database.FieldMessage;
import top.nipuru.minegame.common.message.database.QueryPlayerRequest;
import top.nipuru.minegame.server.BukkitPlugin;
import top.nipuru.minegame.server.Router;
import top.nipuru.minegame.server.game.player.GamePlayer;
import top.nipuru.minegame.server.game.player.DataInfo;
import net.afyer.afybroker.client.Broker;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.net.InetAddress;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class AsyncPlayerPreLoginListener implements Listener {

    private final BukkitPlugin plugin;
    private final Map<UUID, DataInfo> pendingPlayers;

    public AsyncPlayerPreLoginListener(BukkitPlugin plugin, Map<UUID, DataInfo> pendingPlayers) {
        this.plugin = plugin;
        this.pendingPlayers = pendingPlayers;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onEventLow(AsyncPlayerPreLoginEvent event) {
        try {
            // 防止在插件启用之前玩家加入进来
            plugin.getEnableLatch().await();

            this.handlePreLogin(event.getUniqueId(), event.getAddress());
        } catch (Exception e) {
            TextComponent message = Component.text("登录失败！请重试").color(NamedTextColor.RED);
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, message);
            log.error(e.getMessage(), e);
        }
    }

    private void handlePreLogin(UUID uniqueId, InetAddress address) throws Exception {
        // 优先从别的服务器转移数据
        PlayerDataTransferRequest transferRequest = new PlayerDataTransferRequest()
                .setUniqueId(uniqueId);
        PlayerDataTransferRequest.PlayerDataMessage playerDataMessage = Broker.invokeSync(transferRequest);
        int playerId;
        int dbId;
        Map<String, List<List<FieldMessage>>> data;

        if (playerDataMessage != null) {
            data = playerDataMessage.getData();
            playerId = playerDataMessage.getPlayerId();
            dbId = playerDataMessage.getDbId();
        } else {
            // 其他服务器没有数据代表登录 需要新建数据或从数据库加载
            QueryUserRequest queryUserRequest = new QueryUserRequest(uniqueId, address.getHostAddress());
            QueryUserRequest.UserMessage userMessage = Router.authRequest(queryUserRequest);
            QueryPlayerRequest queryPlayerRequest = new QueryPlayerRequest(userMessage.getPlayerId());
            GamePlayer.preload(queryPlayerRequest);
            data = Router.databaseRequest(userMessage.getDbId(), queryPlayerRequest);
            playerId = userMessage.getPlayerId();
            dbId = userMessage.getDbId();
            PlayerRegisterMessage registerMessage = new PlayerRegisterMessage(uniqueId, playerId);
            Broker.oneway(registerMessage);
        }

        DataInfo dataInfo =  new DataInfo(playerId, dbId, data);

        pendingPlayers.put(uniqueId, dataInfo);
    }

    
}
