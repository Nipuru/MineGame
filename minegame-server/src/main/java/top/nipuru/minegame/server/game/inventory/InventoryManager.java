package top.nipuru.minegame.server.game.inventory;

import lombok.extern.slf4j.Slf4j;
import top.nipuru.minegame.common.message.database.QueryPlayerRequest;
import top.nipuru.minegame.server.game.player.BaseManager;
import top.nipuru.minegame.server.game.player.GamePlayer;
import top.nipuru.minegame.server.game.player.DataInfo;
import top.nipuru.minegame.server.util.NmsMethods;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

@Slf4j(topic = "InventoryManager")
public class InventoryManager extends BaseManager {

    private InventoryData data;

    public InventoryManager(GamePlayer player) {
        super(player);
    }

    public static void preload(QueryPlayerRequest request) throws Exception {
        preload(request, InventoryData.class);
    }

    public void unpack(DataInfo dataInfo) throws Exception {
        data = dataInfo.unpack(InventoryData.class);
    }

    public void pack(DataInfo dataInfo) throws Exception {
        dataInfo.pack(data);
    }

    public void onJoin() {
        // 清除玩家数据
        Player bukkitPlayer = player.getBukkitPlayer();
        resetPlayer(bukkitPlayer);
        if (data == null) {
            // 新增数据
            data = new InventoryData();
            savePlayer(bukkitPlayer, data);
            player.insert(data);
            return;
        }
        // 运用数据
        applyPlayer(bukkitPlayer, data);
        log.info("InventoryData has applied for GamePlayer: {}", bukkitPlayer.getName());
    }

    public void onQuit() {
        Player bukkitPlayer = player.getBukkitPlayer();
        // 将光标上的物品尽可能放回背包
        ItemStack itemOnCursor = bukkitPlayer.getItemOnCursor();
        if (!itemOnCursor.getType().isAir()) {
            NmsMethods.placeItemBackInInventory(bukkitPlayer, itemOnCursor);
            bukkitPlayer.setItemOnCursor(null);
        }

        savePlayer(bukkitPlayer, data);
        player.update(data);
    }
    
    private static void resetPlayer(Player bukkitPlayer) {
        bukkitPlayer.getInventory().clear();
        bukkitPlayer.getInventory().setArmorContents(null);
        bukkitPlayer.getEnderChest().clear();
        bukkitPlayer.setExp(0.0F);
        bukkitPlayer.setLevel(0);
        bukkitPlayer.setFoodLevel(20);
        bukkitPlayer.setSaturation(5.0F);
        bukkitPlayer.setGameMode(GameMode.SURVIVAL);
        bukkitPlayer.setMaximumAir(300);
        bukkitPlayer.setRemainingAir(300);
        for (PotionEffect potionEffect : bukkitPlayer.getActivePotionEffects()) {
            bukkitPlayer.removePotionEffect(potionEffect.getType());
        }
        bukkitPlayer.setHealth(20.0);
        bukkitPlayer.setHealthScale(20.0);
        NmsMethods.clearPersistentDataContainer(bukkitPlayer.getPersistentDataContainer());
    }
    
    private static void applyPlayer(Player bukkitPlayer, InventoryData data) {
        if (data.inventory.length != 0) {
            bukkitPlayer.getInventory().setContents(NmsMethods.deserializeIcons(data.inventory));
        }

        bukkitPlayer.getInventory().setHeldItemSlot(data.hotBar);
        bukkitPlayer.setGameMode(GameMode.values()[data.gameMode]);
        if (data.enderChest.length != 0) {
            bukkitPlayer.getEnderChest().setContents(NmsMethods.deserializeIcons(data.enderChest));
        }
        bukkitPlayer.setExp(data.experience);
        bukkitPlayer.setTotalExperience(data.totalExperience);
        bukkitPlayer.setLevel(data.experienceLevel);

        if (data.potionEffects.length != 0) {
            for (PotionEffect potionEffect : NmsMethods.deserializePotionEffects(data.potionEffects)) {
                bukkitPlayer.addPotionEffect(potionEffect);
            }
        }
        bukkitPlayer.setFoodLevel(data.foodLevel);
        bukkitPlayer.setSaturation(data.saturation);
        bukkitPlayer.setRemainingAir(data.air);
        bukkitPlayer.setMaximumAir(data.maxAir);
        if (data.bukkitValues.length != 0) {
            NmsMethods.deserializePersistentDataContainer(bukkitPlayer.getPersistentDataContainer(), data.bukkitValues);
        }
        bukkitPlayer.setFireTicks(data.fireTicks);
        bukkitPlayer.setFreezeTicks(data.freezeTicks);
        bukkitPlayer.setHealth(data.health);
        bukkitPlayer.setHealthScale(data.healthScale);
        bukkitPlayer.setHealthScaled(data.healthScaled);
    }

    private static void savePlayer(Player bukkitPlayer, InventoryData data) {
        data.inventory = NmsMethods.serializeIcons(bukkitPlayer.getInventory().getContents());
        data.hotBar = bukkitPlayer.getInventory().getHeldItemSlot();
        data.gameMode = bukkitPlayer.getGameMode().ordinal();
        data.enderChest = NmsMethods.serializeIcons(bukkitPlayer.getEnderChest().getContents());
        data.experience = bukkitPlayer.getExp();
        data.totalExperience = bukkitPlayer.getTotalExperience();
        data.experienceLevel = bukkitPlayer.getLevel();
        data.potionEffects = NmsMethods.serializePotionEffects(bukkitPlayer.getActivePotionEffects());
        data.health = bukkitPlayer.getHealth();
        data.healthScale = bukkitPlayer.getHealthScale();
        data.healthScaled = bukkitPlayer.isHealthScaled();
        data.foodLevel = bukkitPlayer.getFoodLevel();
        data.saturation = bukkitPlayer.getSaturation();
        data.air = bukkitPlayer.getRemainingAir();
        data.maxAir = bukkitPlayer.getMaximumAir();
        data.bukkitValues = NmsMethods.serializePersistentDataContainer(bukkitPlayer.getPersistentDataContainer());
        data.fireTicks = bukkitPlayer.getFireTicks();
        data.freezeTicks = bukkitPlayer.getFreezeTicks();
        log.info("Saving InventoryData for GamePlayer: {}", bukkitPlayer.getName());
    }
}
