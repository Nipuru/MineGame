package top.nipuru.minegame.game

import net.kyori.adventure.text.format.TextColor
import org.bukkit.command.CommandSender
import java.awt.Color

/**
 * ALLOW正常
 * FAILED失败
 * INFO提示
 * WARNING警告
 *
 * @author Nipuru
 * @since 2024/10/24 14:06
 */
enum class MessageType(color: Color) {
    ALLOW(Color(185, 236, 90)),
    FAILED(Color(242, 223, 84)),
    INFO(Color(98, 205, 228)),
    WARNING(Color(236, 90, 93));

    val chatColor: TextColor = TextColor.color(color.rgb)

    fun sendMessage(sender: CommandSender?, vararg args: Any?) {
        // todo
    }
}
