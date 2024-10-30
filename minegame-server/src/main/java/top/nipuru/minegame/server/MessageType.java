package top.nipuru.minegame.server;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.CommandSender;

import java.awt.*;

/**
 * ALLOW正常
 * FAILED失败
 * INFO提示
 * WARNING警告
 *
 * @author Nipuru
 * @since 2024/10/24 14:06
 */
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum MessageType {
    ALLOW(new Color(185, 236, 90)),
    FAILED(new Color(242, 223, 84)),
    INFO(new Color(98, 205, 228)),
    WARNING(new Color(236, 90, 93));

    final TextColor chatColor;

    MessageType(Color color) {
        this.chatColor = TextColor.color(color.getRGB());
    }

    public void sendMessage(CommandSender sender, Object... args) {
        // todo
    }
}
