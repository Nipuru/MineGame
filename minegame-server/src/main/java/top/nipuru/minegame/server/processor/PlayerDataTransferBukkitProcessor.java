package top.nipuru.minegame.server.processor;

import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.AsyncUserProcessor;
import lombok.extern.slf4j.Slf4j;
import top.nipuru.minegame.common.message.PlayerDataTransferRequest;
import top.nipuru.minegame.server.BukkitPlugin;
import top.nipuru.minegame.server.game.player.GamePlayer;
import top.nipuru.minegame.server.game.player.DataInfo;
import top.nipuru.minegame.server.util.NmsMethods;

import java.util.HashMap;

@Slf4j
public class PlayerDataTransferBukkitProcessor extends AsyncUserProcessor<PlayerDataTransferRequest> {

    private final BukkitPlugin plugin;

    public PlayerDataTransferBukkitProcessor(BukkitPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handleRequest(BizContext bizCtx, AsyncContext asyncCtx, PlayerDataTransferRequest request) throws Exception {
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            GamePlayer player = plugin.getPlayer(request.getUniqueId());
            if (player == null) {
                asyncCtx.sendResponse(null);
                return;
            }

            NmsMethods.freezePlayer(player.getBukkitPlayer());         // 冻结玩家 不处理客户端发包
            NmsMethods.removeFromPlayerList(player.getBukkitPlayer()); // 强制移出玩家列表 触发 PlayerQuitEvent 并完成数据的保存

            DataInfo dataInfo = new DataInfo(player.getPlayerId(), player.getDbId(), new HashMap<>());
            try {
                player.pack(dataInfo);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                asyncCtx.sendException(e);
                return;
            }

            PlayerDataTransferRequest.PlayerDataMessage response = new PlayerDataTransferRequest.PlayerDataMessage()
                    .setPlayerId(dataInfo.getPlayerId())
                    .setDbId(dataInfo.getDbId())
                    .setData(dataInfo.getTables());

            asyncCtx.sendResponse(response);
        });
    }

    @Override
    public String interest() {
        return PlayerDataTransferRequest.class.getName();
    }
}
