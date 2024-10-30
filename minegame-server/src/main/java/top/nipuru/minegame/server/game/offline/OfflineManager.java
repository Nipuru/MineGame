package top.nipuru.minegame.server.game.offline;

import com.alipay.remoting.exception.RemotingException;
import lombok.extern.slf4j.Slf4j;
import top.nipuru.minegame.common.message.PlayerOfflineDataMessage;
import top.nipuru.minegame.common.message.database.QueryPlayerRequest;
import top.nipuru.minegame.server.game.inventory.InventoryData;
import top.nipuru.minegame.server.game.player.BaseManager;
import top.nipuru.minegame.server.game.player.GamePlayer;
import top.nipuru.minegame.server.game.player.DataInfo;
import net.afyer.afybroker.client.Broker;

import java.util.*;

@Slf4j
public class OfflineManager extends BaseManager {

    private final Map<String, OfflineDataHandler> offlineDataHandlers = new HashMap<>();
    private final List<OfflineData> offlineDataList = new ArrayList<>();
    private final LinkedList<PlayerOfflineDataMessage> offlineDataMessageQueue = new LinkedList<>();

    public OfflineManager(GamePlayer player) {
        super(player);
    }

    public static void preload(QueryPlayerRequest request) throws Exception {
        preload(request, InventoryData.class);
    }

    public void unpack(DataInfo dataInfo) throws Exception {
        offlineDataList.addAll(dataInfo.unpackList(OfflineData.class));
    }

    public void pack(DataInfo dataInfo) throws Exception {
        for (OfflineData data : offlineDataList) {
            dataInfo.pack(data);
        }
    }

    public void tick(long systemTimeMillis) {
        this.pushOfflineData();
    }

    public OfflineDataHandler getHandler(String module) {
        return offlineDataHandlers.get(module);
    }

    public void registerHandler(String module, OfflineDataHandler handler) {
        if (this.offlineDataHandlers.containsKey(module)) {
            throw new IllegalArgumentException("Offline handler for module: " + module + " has benn registered");
        }
        this.offlineDataHandlers.put(module, handler);
    }

    public void pushOfflineTo(int playerId, int dbId, String moduleName, String data) {
        PlayerOfflineDataMessage message = new PlayerOfflineDataMessage()
                .setPlayerId(playerId)
                .setDbId(dbId)
                .setModule(moduleName)
                .setData(data);
        // 并不立即发送，而是等到下一tick
        // 为什么这么做呢，因为如果调用此方法的时候，玩家已经离线了，就没必要发送了
        offlineDataMessageQueue.add(message);
    }

    public void onJoin() {
        offlineDataList.removeIf(offlineData -> {
            OfflineDataHandler offlineDataHandler = offlineDataHandlers.get(offlineData.module);
            if (offlineDataHandler == null) {
                return false;
            }
            if (offlineDataHandler.handle(offlineData.data, false)) {
                player.delete(offlineData);
                return true;
            }
            return false;
        });
    }

    private void pushOfflineData() {
        if (offlineDataMessageQueue.isEmpty()) return;
        List<PlayerOfflineDataMessage> messages = new ArrayList<>(offlineDataMessageQueue);
        offlineDataMessageQueue.clear();
        player.getPlugin().getBizThread().submit(() -> {
            try {
                Broker.oneway(messages);
            } catch (RemotingException | InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        });
    }


}
