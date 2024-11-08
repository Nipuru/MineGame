package top.nipuru.minegame.game.gameplay.item

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import top.nipuru.minegame.common.message.database.QueryPlayerRequest
import top.nipuru.minegame.game.constants.ITEM_PROP
import top.nipuru.minegame.game.constants.PROP_COIN
import top.nipuru.minegame.game.constants.PROP_POINTS
import top.nipuru.minegame.game.gameplay.player.BaseManager
import top.nipuru.minegame.game.gameplay.player.DataInfo
import top.nipuru.minegame.game.gameplay.player.GamePlayer
import top.nipuru.minegame.game.gameplay.player.preload
import top.nipuru.minegame.game.logAddItem
import top.nipuru.minegame.game.logSubtractItem
import top.nipuru.minegame.game.logger


class ItemManager(player: GamePlayer) : BaseManager(player) {
    
    private val items: Int2ObjectMap<Int2ObjectMap<ItemData>> = Int2ObjectArrayMap()

    fun preload(request: QueryPlayerRequest) {
        request.preload(ItemData::class.java)
    }
    
    fun unpack(dataInfo: DataInfo) {
        for (item in dataInfo.unpackList(ItemData::class.java)) {
            items.getOrPut(item.itemType) {
                Int2ObjectArrayMap()
            }.put(item.itemId, item)
        }
    }
    
    fun pack(dataInfo: DataInfo) {
        for (byId in items.values) {
            for (item in byId.values) {
                dataInfo.pack<ItemData>(item)
            }
        }
    }

    fun getPropAmount(propId: Int): Long {
        return when (propId) {
            PROP_COIN -> player.coreManager.coin
            PROP_POINTS -> player.coreManager.points
            else -> getItem(ITEM_PROP, propId).amount
        }
    }

    fun addProp(propId: Int, amount: Int, way: Int) {
        if (amount == 0) return
        if (amount < 0) {
            logger.error("add invalid prop amount: {}", amount)
            return
        }
        when (propId) {
            PROP_COIN -> player.coreManager.addCoin(amount, way)
            PROP_POINTS -> player.coreManager.addPoints(amount, way)
            else -> {
                val item = getItem(ITEM_PROP, propId)
                item.amount += amount.toLong()
                logAddItem(player.playerId, ITEM_PROP, propId, amount, way)
                player.update(item, ItemData::amount)
            }
        }
    }

    fun subtractProp(propId: Int, amount: Int, way: Int) {
        if (amount == 0) return
        if (amount < 0) {
            logger.error("subtract invalid prop amount: {}", amount)
            return
        }
        when (propId) {
            PROP_COIN -> player.coreManager.subtractCoin(amount, way)
            PROP_POINTS -> player.coreManager.subtractPoints(amount, way)
            else -> {
                val item = getItem(ITEM_PROP, propId)
                item.amount -= amount.toLong()
                logSubtractItem(player.playerId, ITEM_PROP, propId, amount, way)
                player.update(item, ItemData::amount)
            }
        }
    }

    fun getItemAmount(itemType: Int, itemId: Int): Long {
        if (itemType == ITEM_PROP) return getPropAmount(itemId)
        return getItem(itemType, itemId).amount
    }

    fun addItem(itemType: Int, itemId: Int, amount: Int, way: Int) {
        if (amount == 0) return
        if (amount < 0) {
            logger.error("add invalid item amount: {}", amount)
            return
        }
        if (itemType == ITEM_PROP) {
            addProp(itemId, amount, way)
            return
        }
        val item = getItem(itemType, itemId)
        item.amount += amount.toLong()
        logAddItem(player.playerId, itemType, itemId, amount, way)
        player.update(item, ItemData::amount)
    }

    fun subtractItem(itemType: Int, itemId: Int, amount: Int, way: Int) {
        if (amount == 0) return
        if (amount < 0) {
            logger.error("subtract invalid item amount: {}", amount)
            return
        }
        if (itemType == ITEM_PROP) {
            subtractProp(itemId, amount, way)
            return
        }
        val item = getItem(itemType, itemId)
        item.amount -= amount.toLong()
        logSubtractItem(player.playerId, itemType, itemId, amount, way)
        player.update(item, ItemData::amount)
    }

    private fun getItem(itemType: Int, itemId: Int): ItemData {
        var byId = items[itemType]
        if (byId == null) {
            byId = Int2ObjectArrayMap()
            items.put(itemType, byId)
        }
        var item = byId[itemId]
        if (item == null) {
            item = ItemData()
            byId.put(itemId, item)
            player.insert(item)
        }
        return item
    }
}
