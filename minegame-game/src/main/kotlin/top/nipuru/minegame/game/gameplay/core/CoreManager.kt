package top.nipuru.minegame.game.gameplay.core

import top.nipuru.minegame.common.message.database.QueryPlayerRequest
import top.nipuru.minegame.common.message.shared.PlayerInfoMessage
import top.nipuru.minegame.common.message.shared.PlayerInfoUpdateNotify
import top.nipuru.minegame.game.*
import top.nipuru.minegame.game.constants.ITEM_PROP
import top.nipuru.minegame.game.constants.PROP_COIN
import top.nipuru.minegame.game.constants.PROP_POINTS
import top.nipuru.minegame.game.gameplay.player.BaseManager
import top.nipuru.minegame.game.gameplay.player.DataInfo
import top.nipuru.minegame.game.gameplay.player.GamePlayer
import top.nipuru.minegame.game.gameplay.player.preload

class CoreManager(player: GamePlayer) : BaseManager(player) {
    private lateinit var playerData: PlayerData
    private var playedTimeUpdateTime: Long = 0
    private var updateShared = false

    fun preload(request: QueryPlayerRequest) {
        request.preload(PlayerData::class.java)
    }

    fun unpack(dataInfo: DataInfo) {
        dataInfo.unpack(PlayerData::class.java).let { 
            if (it != null) {
                playerData = it
            } else {
                playerData = PlayerData()
                playerData.createTime = now()
                player.insert(playerData)
                updateShared = true
            }
        }
    }
    
    fun pack(dataInfo: DataInfo) {
        dataInfo.pack(playerData)
    }

    // 更新玩家在线时间
    fun tick(systemTimeMills: Long) {
        updatePlayedTime(systemTimeMills, false)
        updatePublic()
    }

    fun onQuit() {
        updatePlayedTime(System.currentTimeMillis(), true)
    }

    /** 货币  */
    val coin: Long
        get() = playerData.coin

    /** 点券  */
    val points: Long
        get() = playerData.points

    /** 头衔id  */
    var rankId
        get() = playerData.rankId
        set(rankId) {
            if (playerData.rankId == rankId) return
            playerData.rankId = rankId
            player.update(playerData, PlayerData::rankId)
            updateShared = true
        }

    /** 徽章id  */
    var medalId: Int
        get() = playerData.medalId
        set(medalId) {
            if (playerData.medalId == medalId) return
            playerData.medalId = medalId
            player.update(playerData, PlayerData::medalId)
            updateShared = true
        }

    /** 创建时间  */
    val createTime: Long
        get() = playerData.createTime

    /** 最后离线时间  */
    var lastLogoutTime: Long
        get() = playerData.lastLogoutTime
        set(time) {
            if (playerData.lastLogoutTime == time) return
            playerData.lastLogoutTime = time
            player.update(playerData, PlayerData::lastLogoutTime)
            updateShared = true
        }

    /** 重置时间  */
    var resetTime: Long
        get() = playerData.resetTime
        set(time) {
            if (playerData.resetTime == time) return
            playerData.resetTime = time
            player.update(playerData, PlayerData::resetTime)
        }

    /** 累计在线时间  */
    var playedTime: Long
        get() = playerData.playedTime
        set(time) {
            if (playerData.playedTime == time) return
            playerData.playedTime = time
            player.update(playerData, PlayerData::playedTime)
            updateShared = true
        }

    /** 生日 birthday[0]:月,birthday[1]:日  */
    var birthday: IntArray
        get() = playerData.birthday
        set(birthday) {
            if (playerData.birthday.contentEquals(birthday)) return
            playerData.birthday = birthday
            player.update(playerData, PlayerData::birthday)
            updateShared = true
        }

    /** 用于传输 或者显示给其他玩家  */
    val playerInfo: PlayerInfoMessage
        get() = PlayerInfoMessage().apply { 
            playerId = player.playerId
            name = player.name
            dbId = player.dbId
            coin = playerData.coin
            rankId = playerData.rankId
            createTime = playerData.createTime
            lastLogoutTime = playerData.lastLogoutTime
            playedTime = playerData.playedTime
        }

    var isOnline: Boolean
        get() = playerData.lastLogoutTime == 0L
        set(isOnline) {
            if (isOnline) {
                lastLogoutTime = 0L
            } else {
                lastLogoutTime = now()
            }
        }

    fun subtractCoin(amount: Int, way: Int) {
        if (amount == 0) return
        if (amount < 0) {
            logger.error("subtract invalid coin amount: {}", amount)
            return
        }
        playerData.coin -= amount.toLong()
        player.update(playerData, PlayerData::coin)
        updateShared = true
        logAddItem(player.playerId, ITEM_PROP, PROP_COIN, amount, way)
    }

    fun addCoin(amount: Int, way: Int) {
        if (amount == 0) return
        if (amount < 0) {
            logger.error("add invalid coin amount: {}", amount)
            return
        }
        playerData.coin += amount.toLong()
        player.update(playerData, PlayerData::coin)
        updateShared = true
        logAddItem(player.playerId, ITEM_PROP, PROP_COIN, amount, way)
    }

    fun subtractPoints(amount: Int, way: Int) {
        if (amount == 0) return
        if (amount < 0) {
            logger.error("subtract invalid points amount: {}", amount)
            return
        }
        playerData.points -= amount.toLong()
        player.update(playerData, PlayerData::coin)
        logAddItem(player.playerId, ITEM_PROP, PROP_POINTS, amount, way)
    }

    fun addPoints(amount: Int, way: Int) {
        if (amount == 0) return
        if (amount < 0) {
            logger.error("add invalid coin points: {}", amount)
            return
        }
        playerData.points += amount.toLong()
        player.update(playerData, PlayerData::points)
        logAddItem(player.playerId, ITEM_PROP, PROP_POINTS, amount, way)
    }

    private fun updatePlayedTime(systemTimeMills: Long, force: Boolean) {
        if (!isOnline) return  // 不在线直接退出

        val updateTime = playedTimeUpdateTime
        var playedTime = playerData.playedTime
        if (updateTime == 0L) {
            playedTimeUpdateTime = systemTimeMills
            this.playedTime = playedTime
            return
        }
        val delay = (60 * 1000).toLong() // 满一分钟执行一次
        if (!force && (systemTimeMills - updateTime + playedTime) / delay == playedTime / delay) return
        if (logger.isDebugEnabled) {
            logger.info(
                "Update playedTime from {} to {} for GamePlayer {}",
                playedTime,
                playedTime + (systemTimeMills - updateTime),
                player.name
            )
        }
        playedTimeUpdateTime = systemTimeMills
        playedTime += systemTimeMills - updateTime
        this.playedTime = playedTime
    }

    // 更新玩家的公共玩家信息
    private fun updatePublic() {
        if (!updateShared) return
        updateShared = false
        val info: PlayerInfoMessage = playerInfo
        if (logger.isDebugEnabled) {
            logger.debug("Update PlayerInfo to SharedServer for GamePlayer: {}", info.name)
        }
        val notify = PlayerInfoUpdateNotify(info)
        bizThread.execute {
            sharedNotify(notify)
        }
    }
}
