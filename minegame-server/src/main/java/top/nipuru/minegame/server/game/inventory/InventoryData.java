package top.nipuru.minegame.server.game.inventory;

import lombok.Getter;
import lombok.experimental.Accessors;
import top.nipuru.minegame.server.game.player.Data;
import top.nipuru.minegame.server.game.player.Table;

@Getter
@Accessors(fluent = true)
@Table(name = "tb_inventory")
public class InventoryData implements Data {

    /** 玩家背包 */
    byte[] inventory = EMPTY_BYTES;

    /** 玩家快捷栏选中的格子序号 */
    int hotBar;

    /** 玩家的游戏模式 */
    int gameMode;

    /** 末影箱 */
    byte[] enderChest = EMPTY_BYTES;

    /** 经验条进度 */
    float experience;

    /** 总经验值 */
    int totalExperience;

    /** 经验等级 */
    int experienceLevel;

    /** 药水效果 */
    byte[] potionEffects = EMPTY_BYTES;

    /** 生命值 */
    double health;

    /** 生命缩放 */
    double healthScale;

    /** 生命是否缩放 */
    boolean healthScaled;

    /** 饥饿值 */
    int foodLevel;

    /** 饱和度 */
    float saturation;

    /** 空气条 */
    int air;

    /** 最大空气条 */
    int maxAir;

    /** PersistentData 数据 */
    byte[] bukkitValues = EMPTY_BYTES;

    /** 着火游戏刻 */
    int fireTicks;

    /** 冰冻游戏刻 */
    int freezeTicks;
}
