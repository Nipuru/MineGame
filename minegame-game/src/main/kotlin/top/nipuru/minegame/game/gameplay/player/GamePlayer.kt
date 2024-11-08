package top.nipuru.minegame.game.gameplay.player

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import top.nipuru.minegame.common.message.database.QueryPlayerRequest
import top.nipuru.minegame.game.gameplay.chat.ChatManager
import top.nipuru.minegame.game.gameplay.core.CoreManager
import top.nipuru.minegame.game.gameplay.friend.FriendManager
import top.nipuru.minegame.game.gameplay.inventory.InventoryManager
import top.nipuru.minegame.game.gameplay.item.ItemManager
import top.nipuru.minegame.game.gameplay.offline.OfflineDataHandler
import top.nipuru.minegame.game.gameplay.offline.OfflineManager
import top.nipuru.minegame.game.gameplay.setting.SettingManager
import top.nipuru.minegame.game.logger
import java.util.*
import java.util.regex.Pattern
import kotlin.reflect.KProperty1

/**
 * 表示一个玩家，所有 api 都应该由主线程去调用，异步要考虑线程安全问题
 */
class GamePlayer(
    val playerId: Int,
    val dbId: Int,
    val name: String,
    val uniqueId: UUID

) {
    val namePattern: Pattern = Pattern.compile(bukkitPlayer.name, Pattern.CASE_INSENSITIVE)
    val bukkitPlayer: Player
        get() = Bukkit.getPlayer(uniqueId)!!

    val dataManager = DataManager(this)
    val offlineManager = OfflineManager(this)
    val coreManager = CoreManager(this)
    val inventoryManager = InventoryManager(this)
    val friendManager = FriendManager(this)
    val settingManager = SettingManager(this)
    val chatManager = ChatManager(this)
    val itemManager = ItemManager(this)

    /**
     * 预加载数据 (告诉 dbserver 要加载哪些数据)
     */
    fun preload(request: QueryPlayerRequest) {
        offlineManager.preload(request)
        coreManager.preload(request)
        inventoryManager.preload(request)
        friendManager.preload(request)
        settingManager.preload(request)
        chatManager.preload(request)
        itemManager.preload(request)
    }

    /**
     * 数据解包
     */
    fun unpack(dataInfo: DataInfo) {
        offlineManager.unpack(dataInfo)
        coreManager.unpack(dataInfo)
        inventoryManager.unpack(dataInfo)
        friendManager.unpack(dataInfo)
        settingManager.unpack(dataInfo)
        chatManager.unpack(dataInfo)
        itemManager.unpack(dataInfo)
    }

    /**
     * 数据装包
     */
    fun pack(dataInfo: DataInfo) {
        offlineManager.pack(dataInfo)
        coreManager.pack(dataInfo)
        inventoryManager.pack(dataInfo)
        friendManager.pack(dataInfo)
        settingManager.pack(dataInfo)
        chatManager.pack(dataInfo)
        itemManager.pack(dataInfo)
    }

    fun init() {
        logger.info("Init GamePlayer: {}", bukkitPlayer.name)
        friendManager.init()
    }

    /**
     * 新玩家初始化
     */
    fun initNew() {
        logger.info("Init new GamePlayer: {}", bukkitPlayer.name)
    }

    /**
     * 玩家登录执行
     */
    fun onLogin() {
        logger.info("GamePlayer: {} has logged in.", bukkitPlayer.name)
    }

    /**
     * 玩家离线执行
     */
    fun onLogout() {
        logger.info("GamePlayer: {} has logged out.", bukkitPlayer.name)
    }

    /**
     * 玩家加入服务器执行
     */
    fun onJoin() {
        logger.info("GamePlayer: {} has joined", bukkitPlayer.name)

        inventoryManager.onJoin()
        offlineManager.onJoin()
    }

    /**
     * 玩家退出服务器执行
     */
    fun onQuit() {
        logger.info("GamePlayer: {} has quit.", bukkitPlayer.name)

        inventoryManager.onQuit()
        coreManager.onQuit()
    }

    /**
     * 每 server tick 执行，频率不是固定的
     * 玩家一定在线，玩家退出时会调用一次
     */
    fun tick(systemTimeMillis: Long) {
        coreManager.tick(systemTimeMillis)

        // 最后执行
        dataManager.tick()
        offlineManager.tick(systemTimeMillis)
    }

    /**
     * 现实中的新的一天，时间必定为 0 时, 会执行一次。
     * 并且会补不在线的天数，最多 30 天
     */
    fun onNewDay(time: Long) {
        coreManager.resetTime = time
    }

    fun registerOfflineHandler(module: String, handler: OfflineDataHandler) {
        offlineManager.registerHandler(module, handler)
    }

    fun <T : Any> update(data: T, vararg properties: KProperty1<T, *>) {
        dataManager.add(DataAction(DataActionType.UPDATE, data, DataConvertor.getProperty(properties)))
    }

    fun <T: Any> insert(data: T) {
        dataManager.add(DataAction(DataActionType.INSERT, data, null))
    }

    fun <T: Any> delete(data: T) {
        dataManager.add(DataAction(DataActionType.DELETE, data, null))
    }

    fun pushOfflineDataTo(playerId: Int, dbId: Int, moduleName: String, data: String) {
        offlineManager.pushOfflineTo(playerId, dbId, moduleName, data)
    }
}
