package top.nipuru.minegame.game.gameplay.chat.formatter

import net.kyori.adventure.text.Component
import top.nipuru.minegame.common.message.shared.PlayerInfoMessage
import top.nipuru.minegame.game.gameplay.chat.Fragment
import top.nipuru.minegame.game.gameplay.player.GamePlayer

interface MessageFormatter {
    fun parse(player: GamePlayer, vararg args: String?): Fragment?

    fun format(sender: PlayerInfoMessage, receiver: GamePlayer, fragment: Fragment): Component
}
