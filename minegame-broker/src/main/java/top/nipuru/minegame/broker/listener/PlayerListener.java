package top.nipuru.minegame.broker.listener;

import top.nipuru.minegame.broker.BrokerPlugin;
import top.nipuru.minegame.broker.game.GamePlayer;
import net.afyer.afybroker.server.event.PlayerProxyLogoutEvent;
import net.afyer.afybroker.server.plugin.EventHandler;
import net.afyer.afybroker.server.plugin.Listener;

public class PlayerListener implements Listener {

    private final BrokerPlugin plugin;

    public PlayerListener(BrokerPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEvent(PlayerProxyLogoutEvent event) {
        GamePlayer player = plugin.getPlayerList().getPlayer(event.getPlayer().getUniqueId());


        if (player != null) {
            plugin.getPlayerList().removePlayer(player);
        }
    }
}
