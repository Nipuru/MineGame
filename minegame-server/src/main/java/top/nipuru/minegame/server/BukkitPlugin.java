package top.nipuru.minegame.server;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import top.nipuru.minegame.common.ClientTag;
import top.nipuru.minegame.common.processor.RequestDispatcher;
import top.nipuru.minegame.server.exception.PlayerNotExistException;
import top.nipuru.minegame.server.game.player.GamePlayer;
import top.nipuru.minegame.server.game.player.GamePlayerList;
import top.nipuru.minegame.server.game.player.DataInfo;
import top.nipuru.minegame.server.listener.AsyncPlayerPreLoginListener;
import top.nipuru.minegame.server.listener.PlayerChatListener;
import top.nipuru.minegame.server.listener.PlayerJoinListener;
import top.nipuru.minegame.server.listener.PlayerQuitListener;
import top.nipuru.minegame.server.processor.*;
import top.nipuru.minegame.server.task.ServerTickTask;
import net.afyer.afybroker.client.Broker;
import net.afyer.afybroker.client.BrokerClientBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * @author Nipuru
 * @since 2024/9/16 0:17
 */
@Slf4j
@Getter
public class BukkitPlugin extends JavaPlugin {

    private final GamePlayerList playerList = new GamePlayerList();
    private final CountDownLatch enableLatch = new CountDownLatch(1);
    private final ExecutorService bizThread = Executors.newCachedThreadPool(new ThreadFactoryBuilder()
            .setDaemon(false)
            .setNameFormat("MineGame-bizThread-%d")
            .build());

    @Override
    public void onLoad() {
        Broker.buildAction(this::buildBrokerClient);
    }

    private void buildBrokerClient(BrokerClientBuilder builder) {
        RequestDispatcher dispatcher = new RequestDispatcher();

        builder.addTag(ClientTag.GAME);
        builder.registerUserProcessor(dispatcher);
        builder.registerUserProcessor(new PlayerDataTransferBukkitProcessor(this));
        builder.registerUserProcessor(new PlayerOfflineDataBukkitProcessor(this));
        builder.registerUserProcessor(new PlayerChatServerProcessor(this));
        builder.registerUserProcessor(new PlayerPrivateChatServerProcessor(this));
    }

    @Override
    public void onEnable() {
        Bukkit.getScheduler().runTaskTimer(this, new ServerTickTask(this), 1L, 1L);

        Map<UUID, DataInfo> pendingPlayers = new HashMap<>();
        Bukkit.getPluginManager().registerEvents(new AsyncPlayerPreLoginListener(this, pendingPlayers), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(this, pendingPlayers), this);
        Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerChatListener(this), this);

        enableLatch.countDown();
    }

    @SneakyThrows
    @Override
    public void onDisable() {
        bizThread.shutdown();
        bizThread.awaitTermination(1L, TimeUnit.MINUTES);
    }

    public GamePlayer getPlayer(Object bukkitPlayer) throws PlayerNotExistException {
        return playerList.getPlayer(((Player) bukkitPlayer).getUniqueId());
    }

    public GamePlayer getPlayer(UUID uniqueId) throws PlayerNotExistException {
        return playerList.getPlayer(uniqueId);
    }

    public GamePlayer getPlayer(int playerId) throws PlayerNotExistException {
        return playerList.getPlayer(playerId);
    }

    public Collection<GamePlayer> getPlayers() {
        return playerList.getPlayers();
    }

}
