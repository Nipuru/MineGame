package top.nipuru.minegame.server.game.player;

import lombok.extern.slf4j.Slf4j;
import top.nipuru.minegame.common.message.database.FieldMessage;
import top.nipuru.minegame.common.message.database.PlayerTransactionRequest;
import top.nipuru.minegame.server.Router;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
public class DataManager {

    private final Queue<DataAction> writeQueue = new ConcurrentLinkedQueue<>();
    private final GamePlayer player;

    public DataManager(GamePlayer player) {
        this.player = player;
    }

    public void add(DataAction info) {
        writeQueue.add(info);
    }

    public void tick(long systemTimeMillis) {
        this.writeDatabase();
    }

    // 玩家数据写入 DBServer
    private void writeDatabase() {
        if (writeQueue.isEmpty()) return;
        // 确保每条数据 只会产生一次提交
        Map<Object, DataAction> map = new IdentityHashMap<>();
        while (!writeQueue.isEmpty()) {
            DataAction dataAction = writeQueue.poll();
            if (map.containsKey(dataAction.getData())) {
                DataAction old = map.remove(dataAction.getData());
                if (old.getType() == DataAction.Type.DELETE || dataAction.getType() == DataAction.Type.INSERT) {
                    // 通常是代码写的有问题 对象删除之后就不能操作了 在新增前不能有任何操作
                    log.error("Invalid player data operation {} before {}, dataClass: {}", old.getType(), dataAction.getType(), dataAction.getData().getClass().getName());
                    kickPlayerIfPossible(player);
                    return; // 跳过本次的操作
                }

                if (dataAction.getType() == DataAction.Type.UPDATE) {
                    if (old.getType() == DataAction.Type.UPDATE) {
                        // 都是更新则进行合并
                        Set<String> mergedField = new HashSet<>();
                        mergedField.addAll(Arrays.asList(dataAction.getFields()));
                        mergedField.addAll(Arrays.asList(old.getFields()));
                        dataAction = new DataAction(DataAction.Type.UPDATE, dataAction.getData(), mergedField.toArray(new String[0]));
                    } else if (old.getType() == DataAction.Type.INSERT) {
                        // 之前是新增则直接新增
                        dataAction = new DataAction(DataAction.Type.INSERT, dataAction.getData(), null);
                    }
                }

                // 这种情况产生一条删除提交 覆盖掉新增 不会出现报错
                // if (writeDBInfo.getType() == WriteDBInfo.Type.DELETE && old.getType() == WriteDBInfo.Type.INSERT);

            }
            map.put(dataAction.getData(), dataAction);
        }
        if (log.isDebugEnabled()) {
            log.info("Write database for player: {}, list: \n{}", player.getName(), map.values().stream()
                    .map(info -> String.format("Type: %s, DataClass: %s, Fields: %s",
                            info.getType(),
                            info.getData().getClass().getName(),
                            info.getFields() != null ? Arrays.toString(info.getFields()) : "null"))
                    .reduce("", (s1, s2) -> s1 + "\n" + s2)
            );
        }
        player.getPlugin().getBizThread().submit(() -> {
            try {
                PlayerTransactionRequest request = new PlayerTransactionRequest(player.getPlayerId());
                for (DataAction dataAction : map.values()) {
                    DataConvertor.DataClassCache dataClassCache = DataConvertor.getOrCache(dataAction.getData().getClass());
                    String tableName = dataClassCache.tableName();

                    List<FieldMessage> uniqueFields = new ArrayList<>();
                    for (String uniqueFieldName : dataClassCache.uniqueFields()) {
                        Field field = dataClassCache.tableFields().get(uniqueFieldName);
                        FieldMessage fieldMessage = new FieldMessage()
                                .setName(uniqueFieldName)
                                .setValue(field.get(dataAction.getData()));
                        uniqueFields.add(fieldMessage);
                    }
                    if (dataAction.getType() == DataAction.Type.UPDATE) {
                        List<FieldMessage> updateFields = new ArrayList<>();
                        for (Map.Entry<String, Field> entry : dataClassCache.updateFields().entrySet()) {
                            FieldMessage fieldMessage = new FieldMessage()
                                    .setName(entry.getKey())
                                    .setValue(entry.getValue().get(dataAction.getData()));
                            updateFields.add(fieldMessage);
                        }
                        request.getUpdates().add(new PlayerTransactionRequest.Update(tableName, uniqueFields, updateFields));
                    } else if (dataAction.getType() == DataAction.Type.INSERT) {
                        for (Map.Entry<String, Field> entry : dataClassCache.updateFields().entrySet()) {
                            FieldMessage fieldMessage = new FieldMessage()
                                    .setName(entry.getKey())
                                    .setValue(entry.getValue().get(dataAction.getData()));
                            uniqueFields.add(fieldMessage);
                        }
                        request.getInserts().add(new PlayerTransactionRequest.Insert(tableName, uniqueFields));
                    } else if (dataAction.getType() == DataAction.Type.DELETE) {
                        request.getDeletes().add(new PlayerTransactionRequest.Delete(tableName, uniqueFields));
                    }
                }
                Router.databaseRequest(player.getDbId(), request);
            } catch (Exception e) {
                log.error("Failed to write database for player {}", player.getPlayerId(), e);
                kickPlayerIfPossible(player);
            }
        });
    }

    private static void kickPlayerIfPossible(GamePlayer player) {
        Player bukkitPlayer = Bukkit.getPlayer(player.getUniqueId());
        if (bukkitPlayer == null) return;
        if (Bukkit.isPrimaryThread()) bukkitPlayer.kick();
        else Bukkit.getScheduler().runTask(player.getPlugin(), (Runnable) bukkitPlayer::kick);
    }
}
