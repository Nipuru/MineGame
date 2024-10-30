package top.nipuru.minegame.server.game.item;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import lombok.extern.slf4j.Slf4j;
import top.nipuru.minegame.common.message.database.QueryPlayerRequest;
import top.nipuru.minegame.server.LogServer;
import top.nipuru.minegame.server.constants.ItemTypes;
import top.nipuru.minegame.server.game.player.BaseManager;
import top.nipuru.minegame.server.game.player.GamePlayer;
import top.nipuru.minegame.server.game.player.DataInfo;

@Slf4j
public class ItemManager extends BaseManager {

    private final Int2ObjectMap<Int2ObjectMap<ItemData>> items = new Int2ObjectArrayMap<>();

    public ItemManager(GamePlayer player) {
        super(player);
    }

    public static void preload(QueryPlayerRequest request) throws Exception {
        preload(request, ItemData.class);
    }

    public void unpack(DataInfo dataInfo) throws Exception {
        for (ItemData item : dataInfo.unpackList(ItemData.class)) {
            items.computeIfAbsent(item.itemType, k -> new Int2ObjectArrayMap<>())
                    .put(item.itemId, item);
        }
    }

    public void pack(DataInfo dataInfo) throws Exception {
        for (Int2ObjectMap<ItemData> byId : items.values()) {
            for (ItemData item : byId.values()) {
                dataInfo.pack(item);
            }
        }
    }

    public long getPropAmount(int propId) {
        return switch (propId) {
            case ItemTypes.PROP_COIN -> player.getCoreManager().getPlayerData().coin();
            case ItemTypes.PROP_POINTS -> player.getCoreManager().getPlayerData().points();
            default -> getItem(ItemTypes.ITEM_PROP, propId).amount; // 其他的当做 item 处理
        };
    }

    public void addProp(int propId, int amount, int way) {
        if (amount == 0) return;
        if (amount < 0) {
            log.error("add invalid prop amount: {}", amount);
            return;
        }
        switch (propId) {
            case ItemTypes.PROP_COIN -> player.getCoreManager().addCoin(amount, way);
            case ItemTypes.PROP_POINTS -> player.getCoreManager().addPoints(amount, way);
            default -> {
                ItemData item = getItem(ItemTypes.ITEM_PROP, propId);
                item.amount += amount;
                LogServer.logAddItem(player.getPlayerId(), ItemTypes.ITEM_PROP, propId, amount, way);
                player.update(item, ItemData::amount);
            }
        }
    }

    public void subtractProp(int propId, int amount, int way) {
        if (amount == 0) return;
        if (amount < 0) {
            log.error("subtract invalid prop amount: {}", amount);
            return;
        }
        switch (propId) {
            case ItemTypes.PROP_COIN -> player.getCoreManager().subtractCoin(amount, way);
            case ItemTypes.PROP_POINTS -> player.getCoreManager().subtractPoints(amount, way);
            default -> {
                ItemData item = getItem(ItemTypes.ITEM_PROP, propId);
                item.amount -= amount;
                LogServer.logSubtractItem(player.getPlayerId(), ItemTypes.ITEM_PROP, propId, amount, way);
                player.update(item, ItemData::amount);
            }
        }
    }

    public long getItemAmount(int itemType, int itemId) {
        if (itemType == ItemTypes.ITEM_PROP) return getPropAmount(itemId);
        return getItem(itemType, itemId).amount;
    }

    public void addItem(int itemType, int itemId, int amount, int way) {
        if (amount == 0) return;
        if (amount < 0) {
            log.error("add invalid item amount: {}", amount);
            return;
        }
        if (itemType == ItemTypes.ITEM_PROP) {
            addProp(itemId, amount, way);
            return;
        }
        ItemData item = getItem(itemType, itemId);
        item.amount += amount;
        LogServer.logAddItem(player.getPlayerId(), itemType, itemId, amount, way);
        player.update(item, ItemData::amount);
    }

    public void subtractItem(int itemType, int itemId, int amount, int way) {
        if (amount == 0) return;
        if (amount < 0) {
            log.error("subtract invalid item amount: {}", amount);
            return;
        }
        if (itemType == ItemTypes.ITEM_PROP) {
            subtractProp(itemId, amount, way);
            return;
        }
        ItemData item = getItem(itemType, itemId);
        item.amount -= amount;
        LogServer.logSubtractItem(player.getPlayerId(), itemType, itemId, amount, way);
        player.update(item, ItemData::amount);
    }

    private ItemData getItem(int itemType, int itemId) {
        Int2ObjectMap<ItemData> byId = items.get(itemType);
        if (byId == null) {
            byId = new Int2ObjectArrayMap<>();
            items.put(itemType, byId);
        }
        ItemData item = byId.get(itemId);
        if (item == null) {
            item = new ItemData();
            byId.put(itemId, item);
            player.insert(item);
        }
        return item;
    }
}
