package top.nipuru.minegame.server.game.chat.formatter;

import top.nipuru.minegame.common.message.shared.PlayerInfoMessage;
import top.nipuru.minegame.server.game.chat.Fragment;
import top.nipuru.minegame.server.game.player.GamePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.Nullable;

public class PlainMessageFormatter implements MessageFormatter {

    @Nullable
    @Override
    public Fragment parse(GamePlayer player, String... args) {
        String rawMessage = args[0];
        if (rawMessage.isEmpty()) return null;
        return new Fragment(rawMessage);
    }

    @Override
    public Component format(PlayerInfoMessage sender, GamePlayer receiver, Fragment fragment) {
        TextComponent.Builder builder = Component.text();
        String rawMessage = fragment.getArg(0);
        if (!sender.getName().equals(receiver.getName())) {
            String[] split = receiver.getNamePattern().split(rawMessage, -1);
            for (int i = 0; i < split.length; i++) {
                builder.append(Component.text(split[i]));
                if (i < split.length - 1) {
                    builder.append(Component.text(receiver.getName()).color(NamedTextColor.GOLD));
                }
            }
        } else {
            builder.content(rawMessage);
        }
        return builder.build();
    }
}
