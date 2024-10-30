package top.nipuru.minegame.server.processor;

import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.AsyncUserProcessor;
import top.nipuru.minegame.common.message.PlayerChatMessage;
import top.nipuru.minegame.server.BukkitPlugin;
import top.nipuru.minegame.server.game.player.GamePlayer;

public class PlayerChatServerProcessor extends AsyncUserProcessor<PlayerChatMessage> {

    private final BukkitPlugin plugin;

    public PlayerChatServerProcessor(BukkitPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handleRequest(BizContext bizContext, AsyncContext asyncContext, PlayerChatMessage request) throws Exception {
        for (GamePlayer player : plugin.getPlayers()) {
            player.getChatManager().receivePublic(request.getSender(), request.getFragments());
        }
    }

    @Override
    public String interest() {
        return PlayerChatMessage.class.getName();
    }

}
