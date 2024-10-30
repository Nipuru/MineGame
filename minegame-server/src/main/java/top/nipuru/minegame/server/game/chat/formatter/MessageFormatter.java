package top.nipuru.minegame.server.game.chat.formatter;

import top.nipuru.minegame.common.message.shared.PlayerInfoMessage;
import top.nipuru.minegame.server.game.chat.Fragment;
import top.nipuru.minegame.server.game.player.GamePlayer;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

public interface MessageFormatter {

    @Nullable Fragment parse(GamePlayer player, String... args);

    Component format(PlayerInfoMessage sender, GamePlayer receiver, Fragment fragment);
}
