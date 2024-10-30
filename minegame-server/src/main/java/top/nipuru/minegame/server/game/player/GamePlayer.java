package top.nipuru.minegame.server.game.player;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import top.nipuru.minegame.common.message.database.QueryPlayerRequest;
import top.nipuru.minegame.server.BukkitPlugin;
import top.nipuru.minegame.server.game.chat.ChatManager;
import top.nipuru.minegame.server.game.core.CoreManager;
import top.nipuru.minegame.server.game.friend.FriendManager;
import top.nipuru.minegame.server.game.inventory.InventoryManager;
import top.nipuru.minegame.server.game.item.ItemManager;
import top.nipuru.minegame.server.game.offline.OfflineDataHandler;
import top.nipuru.minegame.server.game.offline.OfflineManager;
import top.nipuru.minegame.server.game.setting.SettingManager;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.regex.Pattern;

/**
 * 表示一个玩家，所有 api 都应该由主线程去调用，异步要考虑线程安全问题
 */
@Slf4j
@Getter
public class GamePlayer {

    private final BukkitPlugin plugin;
    private final int playerId;
    private final int dbId;
    private final Player bukkitPlayer;
    private final Pattern namePattern;

    private final DataManager dataManager = new DataManager(this);
    private final OfflineManager offlineManager = new OfflineManager(this);
    private final CoreManager coreManager = new CoreManager(this);
    private final InventoryManager inventoryManager = new InventoryManager(this);
    private final FriendManager friendManager = new FriendManager(this);
    private final SettingManager settingManager = new SettingManager(this);
    private final ChatManager chatManager = new ChatManager(this);
    private final ItemManager itemManager = new ItemManager(this);

    public GamePlayer(BukkitPlugin plugin, int playerId, int dbId, Player bukkitPlayer) {
        this.plugin = plugin;
        this.playerId = playerId;
        this.dbId = dbId;
        this.bukkitPlayer = bukkitPlayer;
        this.namePattern = Pattern.compile(bukkitPlayer.getName(), Pattern.CASE_INSENSITIVE);
    }

    /**
     * 预加载数据 (告诉 dbserver 要加载哪些数据)
     */
    public static void preload(QueryPlayerRequest request) throws Exception {
        OfflineManager.preload(request);
        CoreManager.preload(request);
        InventoryManager.preload(request);
        FriendManager.preload(request);
        SettingManager.preload(request);
        ChatManager.preload(request);
        ItemManager.preload(request);
    }

    /**
     * 数据解包
     */
    public void unpack(DataInfo dataInfo) throws Exception {
        offlineManager.unpack(dataInfo);
        coreManager.unpack(dataInfo);
        inventoryManager.unpack(dataInfo);
        friendManager.unpack(dataInfo);
        settingManager.unpack(dataInfo);
        chatManager.unpack(dataInfo);
        itemManager.unpack(dataInfo);
    }

    /**
     * 数据装包
     */
    public void pack(DataInfo dataInfo) throws Exception {
        offlineManager.pack(dataInfo);
        coreManager.pack(dataInfo);
        inventoryManager.pack(dataInfo);
        friendManager.pack(dataInfo);
        settingManager.pack(dataInfo);
        chatManager.pack(dataInfo);
        itemManager.pack(dataInfo);
    }

    public void init() {
        log.info("Init GamePlayer: {}", bukkitPlayer.getName());
        friendManager.init();
    }

    /**
     * 新玩家初始化
     */
    public void initNew() {
        log.info("Init new GamePlayer: {}", bukkitPlayer.getName());
    }

    /**
     * 玩家登录执行
     */
    public void onLogin() {
        log.info("GamePlayer: {} has logged in.", bukkitPlayer.getName());
    }

    /**
     * 玩家离线执行
     */
    public void onLogout() {
        log.info("GamePlayer: {} has logged out.", bukkitPlayer.getName());
    }

    /**
     * 玩家加入服务器执行
     */
    public void onJoin() {
        log.info("GamePlayer: {} has joined", bukkitPlayer.getName());

        inventoryManager.onJoin();
        offlineManager.onJoin();
    }

    /**
     * 玩家退出服务器执行
     */
    public void onQuit() {
        log.info("GamePlayer: {} has quit.", bukkitPlayer.getName());

        inventoryManager.onQuit();
        coreManager.onQuit();
    }

    /**
     * 每 server tick 执行，频率不是固定的
     * 玩家一定在线，玩家退出时会调用一次
     */
    public void tick(long systemTimeMillis) {
        coreManager.tick(systemTimeMillis);

        // 最后执行
        dataManager.tick(systemTimeMillis);
        offlineManager.tick(systemTimeMillis);
    }

    /**
     * 现实中的新的一天，时间必定为 0 时, 会执行一次。
     * 并且会补不在线的天数，最多 30 天
     */
    public void onNewDay(long time) {
        coreManager.setResetTime(time);
    }

    public String getName() {
        return bukkitPlayer.getName();
    }

    public UUID getUniqueId() {
        return bukkitPlayer.getUniqueId();
    }

    public void registerOfflineHandler(String module, OfflineDataHandler handler) {
        offlineManager.registerHandler(module, handler);
    }

    @SafeVarargs
    public final <T> void update(T data, SFunction<T, ?>... fields) {
        dataManager.add(new DataAction(DataAction.Type.UPDATE, data, DataConvertor.getFields(data, fields)));
    }

    public <T> void insert(T data) {
        dataManager.add(new DataAction(DataAction.Type.INSERT, data, null));
    }

    public <T> void delete(T data) {
        dataManager.add(new DataAction(DataAction.Type.DELETE, data, null));
    }

    public void pushOfflineDataTo(int playerId, int dbId, String moduleName, String data) {
        offlineManager.pushOfflineTo(playerId, dbId, moduleName, data);
    }
}
