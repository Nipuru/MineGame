package top.nipuru.minegame.game.gameplay.inventory

import org.bukkit.GameMode
import top.nipuru.minegame.common.message.database.QueryPlayerRequest
import top.nipuru.minegame.game.gameplay.player.BaseManager
import top.nipuru.minegame.game.gameplay.player.DataInfo
import top.nipuru.minegame.game.gameplay.player.GamePlayer
import top.nipuru.minegame.game.gameplay.player.preload
import top.nipuru.minegame.game.logger
import top.nipuru.minegame.game.nms.*

class InventoryManager(player: GamePlayer) : BaseManager(player) {
    private var data: InventoryData? = null

    fun preload(request: QueryPlayerRequest) {
        request.preload(InventoryData::class.java)
    }

    fun unpack(dataInfo: DataInfo) {
        data = dataInfo.unpack(InventoryData::class.java)
    }

    fun pack(dataInfo: DataInfo) {
        dataInfo.pack(data!!)
    }

    fun onJoin() {
        // 清除玩家数据
        val bukkitPlayer: org.bukkit.entity.Player = player.bukkitPlayer
        resetPlayer(bukkitPlayer)
        if (data == null) {
            // 新增数据
            data = InventoryData()
            savePlayer(bukkitPlayer, data!!)
            player.insert(data!!)
            return
        }
        // 运用数据
        applyPlayer(bukkitPlayer, data!!)

    }

    fun onQuit() {
        val bukkitPlayer: org.bukkit.entity.Player = player.bukkitPlayer
        // 将光标上的物品尽可能放回背包
        val itemOnCursor = bukkitPlayer.itemOnCursor
        if (!itemOnCursor.type.isAir) {
            bukkitPlayer.placeBackInInventory(itemOnCursor)
            bukkitPlayer.setItemOnCursor(null)
        }

        savePlayer(bukkitPlayer, data!!)
        player.update(data!!)
    }

    private fun resetPlayer(bukkitPlayer: org.bukkit.entity.Player) {
        bukkitPlayer.inventory.clear()
        bukkitPlayer.inventory.setArmorContents(null)
        bukkitPlayer.enderChest.clear()
        bukkitPlayer.exp = 0.0f
        bukkitPlayer.level = 0
        bukkitPlayer.foodLevel = 20
        bukkitPlayer.saturation = 5.0f
        bukkitPlayer.gameMode = GameMode.SURVIVAL
        bukkitPlayer.maximumAir = 300
        bukkitPlayer.remainingAir = 300
        for (potionEffect in bukkitPlayer.activePotionEffects) {
            bukkitPlayer.removePotionEffect(potionEffect.type)
        }
        bukkitPlayer.health = 20.0
        bukkitPlayer.healthScale = 20.0
        bukkitPlayer.persistentDataContainer.clear()
    }

    private fun applyPlayer(bukkitPlayer: org.bukkit.entity.Player, data: InventoryData) {
        if (data.inventory.isNotEmpty()) {
            bukkitPlayer.inventory.contents = data.inventory.deserializeItemStacks()
        }

        bukkitPlayer.inventory.heldItemSlot = data.hotBar
        bukkitPlayer.gameMode = GameMode.values()[data.gameMode]
        if (data.enderChest.isNotEmpty()) {
            bukkitPlayer.enderChest.contents = data.enderChest.deserializeItemStacks()
        }
        bukkitPlayer.exp = data.experience
        bukkitPlayer.totalExperience = data.totalExperience
        bukkitPlayer.level = data.experienceLevel

        if (data.potionEffects.isNotEmpty()) {
            for (potionEffect in data.potionEffects.deserializePotionEffects()) {
                bukkitPlayer.addPotionEffect(potionEffect)
            }
        }
        bukkitPlayer.foodLevel = data.foodLevel
        bukkitPlayer.saturation = data.saturation
        bukkitPlayer.remainingAir = data.air
        bukkitPlayer.maximumAir = data.maxAir
        if (data.bukkitValues.isNotEmpty()) {
            bukkitPlayer.persistentDataContainer.deserialize(data.bukkitValues)
        }
        bukkitPlayer.fireTicks = data.fireTicks
        bukkitPlayer.freezeTicks = data.freezeTicks
        bukkitPlayer.health = data.health
        bukkitPlayer.healthScale = data.healthScale
        bukkitPlayer.isHealthScaled = data.healthScaled
        logger.info("InventoryData has applied for GamePlayer: {}", bukkitPlayer.name)
    }

    private fun savePlayer(bukkitPlayer: org.bukkit.entity.Player, data: InventoryData) {
        data.inventory = bukkitPlayer.inventory.contents.serialize()
        data.hotBar = bukkitPlayer.inventory.heldItemSlot
        data.gameMode = bukkitPlayer.gameMode.ordinal
        data.enderChest = bukkitPlayer.enderChest.contents.serialize()
        data.experience = bukkitPlayer.exp
        data.totalExperience = bukkitPlayer.totalExperience
        data.experienceLevel = bukkitPlayer.level
        data.potionEffects = bukkitPlayer.activePotionEffects.serialize()
        data.health = bukkitPlayer.health
        data.healthScale = bukkitPlayer.healthScale
        data.healthScaled = bukkitPlayer.isHealthScaled
        data.foodLevel = bukkitPlayer.foodLevel
        data.saturation = bukkitPlayer.saturation
        data.air = bukkitPlayer.remainingAir
        data.maxAir = bukkitPlayer.maximumAir
        data.bukkitValues = bukkitPlayer.persistentDataContainer.serialize()
        data.fireTicks = bukkitPlayer.fireTicks
        data.freezeTicks = bukkitPlayer.freezeTicks
        logger.info("Saving InventoryData for GamePlayer: {}", bukkitPlayer.name)
    }
}
