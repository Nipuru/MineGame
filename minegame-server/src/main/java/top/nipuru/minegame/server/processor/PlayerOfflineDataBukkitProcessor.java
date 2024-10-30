package top.nipuru.minegame.server.processor;

import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.AsyncUserProcessor;
import top.nipuru.minegame.common.message.PlayerOfflineDataMessage;
import top.nipuru.minegame.server.BukkitPlugin;
import top.nipuru.minegame.server.game.offline.OfflineDataHandler;
import top.nipuru.minegame.server.game.player.GamePlayer;
import org.bukkit.Bukkit;

public class PlayerOfflineDataBukkitProcessor extends AsyncUserProcessor<PlayerOfflineDataMessage> {

    private final BukkitPlugin plugin;

    public PlayerOfflineDataBukkitProcessor(BukkitPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handleRequest(BizContext bizContext, AsyncContext asyncContext, PlayerOfflineDataMessage request) throws Exception {
        Bukkit.getScheduler().runTask(plugin, () -> {
            GamePlayer gamePlayer = plugin.getPlayer(request.getPlayerId());
            if (gamePlayer == null) {
                asyncContext.sendResponse(false);
                return;
            }
            OfflineDataHandler handler = gamePlayer.getOfflineManager().getHandler(request.getModule());
            if (handler == null) {
                asyncContext.sendResponse(false);
                return;
            }
            boolean result = handler.handle(request.getData(), true);
            asyncContext.sendResponse(result);
        });
    }

    @Override
    public String interest() {
        return PlayerOfflineDataMessage.class.getName();
    }
}
