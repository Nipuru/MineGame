package top.nipuru.minegame.server.processor;

import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.SyncUserProcessor;
import top.nipuru.minegame.common.message.PlayerChatMessage;
import top.nipuru.minegame.common.message.PlayerPrivateChatMessage;
import top.nipuru.minegame.server.BukkitPlugin;
import top.nipuru.minegame.server.game.player.GamePlayer;
import org.bukkit.Bukkit;

public class PlayerPrivateChatServerProcessor extends SyncUserProcessor<PlayerPrivateChatMessage> {

    private final BukkitPlugin plugin;

    public PlayerPrivateChatServerProcessor(BukkitPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Object handleRequest(BizContext bizContext, PlayerPrivateChatMessage request) {
        try {
            GamePlayer player = plugin.getPlayer(Bukkit.getPlayerExact(request.getReceiver()));
            if (!player.getChatManager().couldReceivePrivate(request.getSender())) {
                return PlayerPrivateChatMessage.DENY;
            }
            player.getChatManager().receivePrivate(request.getSender(), request.getFragments());
        } catch (Exception ignored) {} // 有概率玩家在跨服或者离线
        return PlayerPrivateChatMessage.SUCCESS;
    }


    @Override
    public String interest() {
        return PlayerChatMessage.class.getName();
    }

}
