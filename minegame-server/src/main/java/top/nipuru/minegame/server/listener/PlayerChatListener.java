package top.nipuru.minegame.server.listener;

import io.papermc.paper.event.player.AsyncChatEvent;
import lombok.extern.slf4j.Slf4j;
import top.nipuru.minegame.server.BukkitPlugin;
import top.nipuru.minegame.server.exception.IgnoredException;
import top.nipuru.minegame.server.game.chat.ChatManager;
import top.nipuru.minegame.server.game.player.GamePlayer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@Slf4j
public class PlayerChatListener implements Listener {

    private final BukkitPlugin plugin;

    public PlayerChatListener(BukkitPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEvent(AsyncChatEvent event) {
        event.setCancelled(true);
        String message = LegacyComponentSerializer.legacySection().serialize(event.message());
        try {
            handleChat(event.getPlayer(), message);
        } catch (IgnoredException e) {
            // ignore
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void handleChat(Player bukkitPlayer, String message) {
        GamePlayer player = plugin.getPlayer(bukkitPlayer);
        ChatManager chatManager = player.getChatManager();
        Bukkit.getScheduler().runTask(plugin, () -> {
            if (chatManager.isMuted()) {
                return;
            }
            if (chatManager.hasMsgTarget()) {
                player.getChatManager().sendPrivate(chatManager.getMsgTarget(), message);
            } else {
                player.getChatManager().sendPublic(message);
            }
        });
    }
}
