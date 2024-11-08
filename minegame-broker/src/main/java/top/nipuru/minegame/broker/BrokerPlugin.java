package top.nipuru.minegame.broker;

import top.nipuru.minegame.broker.game.GamePlayer;
import top.nipuru.minegame.broker.game.GamePlayerList;
import top.nipuru.minegame.broker.processor.*;
import top.nipuru.minegame.common.ClientType;
import lombok.Getter;
import net.afyer.afybroker.server.plugin.Plugin;
import net.afyer.afybroker.server.proxy.BrokerClientItem;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@Getter
public class BrokerPlugin extends Plugin {

    private final GamePlayerList playerList = new GamePlayerList();

    @Override
    public void onEnable() {
        getServer().registerUserProcessor(new RequestMessageRouter(this));

        getServer().registerUserProcessor(new PlayerDataTransferBrokerProcessor());
        getServer().registerUserProcessor(new PlayerOfflineDataBrokerProcessor(this));
        getServer().registerUserProcessor(new PlayerChatBrokerProcessor(this));
        getServer().registerUserProcessor(new PlayerPrivateChatBrokerProcessor(this));
        getServer().registerUserProcessor(new PlayerRegisterBrokerProcessor(this));
    }

    @Nullable
    public BrokerClientItem getDbServer(int dbId) {
        String dbServerName = String.format("%s-%d", ClientType.DB, dbId);
        return getServer().getClient(dbServerName);
    }

    @Override
    public void onDisable() {

    }

    public GamePlayer getPlayer(int playerId) {
        return playerList.getPlayer(playerId);
    }

    public GamePlayer getPlayer(UUID uniqueId) {
        return playerList.getPlayer(uniqueId);
    }
}
