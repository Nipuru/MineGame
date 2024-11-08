package top.nipuru.minegame.game.gameplay.chat

import net.afyer.afybroker.client.Broker
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import top.nipuru.minegame.common.message.FragmentMessage
import top.nipuru.minegame.common.message.PlayerChatMessage
import top.nipuru.minegame.common.message.PlayerPrivateChatMessage
import top.nipuru.minegame.common.message.database.QueryPlayerRequest
import top.nipuru.minegame.common.message.shared.PlayerInfoMessage
import top.nipuru.minegame.game.MessageType
import top.nipuru.minegame.game.gameplay.player.BaseManager
import top.nipuru.minegame.game.gameplay.player.DataInfo
import top.nipuru.minegame.game.gameplay.player.GamePlayer
import top.nipuru.minegame.game.gameplay.player.preload
import top.nipuru.minegame.game.now
import top.nipuru.minegame.game.plugin
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class ChatManager(player: GamePlayer) : BaseManager(player) {

    private lateinit var data: ChatData

    fun preload(request: QueryPlayerRequest) {
        request.preload(ChatData::class.java)
    }
    fun unpack(dataInfo: DataInfo) {
        dataInfo.unpack(ChatData::class.java).let {
            if (it != null) {
                data = it
            } else {
                data = ChatData()
                player.insert(data)
            }
        }
    }

    fun pack(dataInfo: DataInfo) {
        dataInfo.pack(data)
    }

    var msgTarget: String
        get() = data.msgTarget
        set(msgTarget) {
            data.msgTarget = msgTarget
        }

    fun hasMsgTarget(): Boolean {
        return data.msgTarget.isNotEmpty()
    }

    fun clearMsgTarget() {
        data.msgTarget = ""
    }

    var mute: Long
        get() = data.mute
        set(time) {
            data.mute = time
            player.update(data, ChatData::mute)
        }

    val isMuted: Boolean
        get() = data.mute > now()

    fun unmute() {
        val now: Long = now()
        if (data.mute == now) return
        data.mute = now
        player.update(data, ChatData::mute)
    }

    val muteDateTime: LocalDateTime
        get() {
            val dateTime = Instant.ofEpochMilli(data.mute)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()

            return dateTime
        }

    fun parseMessage(message: String): Array<FragmentMessage> {
        return MessageFormat.parse(player, message)
    }

    fun couldReceivePrivate(sender: PlayerInfoMessage): Boolean {
        return true
    }

    fun receivePublic(sender: PlayerInfoMessage, fragments: Array<FragmentMessage>) {
        val builder = Component.text()
        builder.color(NamedTextColor.WHITE)
        builder.append(Component.text(sender.name + ": "))
        builder.append(MessageFormat.format(sender, player, fragments))
        player.bukkitPlayer.sendMessage(builder.build())
    }

    fun receivePrivate(sender: PlayerInfoMessage, fragments: Array<FragmentMessage>) {
        val builder = Component.text()
        builder.color(NamedTextColor.WHITE)
        builder.append(Component.text(sender.name + " 对你说: "))
        builder.append(MessageFormat.format(sender, player, fragments))
        player.bukkitPlayer.sendMessage(builder.build())
    }

    fun sendPublic(message: String) {
        val fragments = MessageFormat.parse(player, message)
        val request = PlayerChatMessage(player.coreManager.playerInfo, fragments)

        Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
            val result = Broker.invokeSync<Int>(request)
            when (result) {
                PlayerChatMessage.SUCCESS -> {}
                PlayerChatMessage.FAILURE -> MessageType.FAILED.sendMessage(player.bukkitPlayer, "消息发送失败。")
                PlayerChatMessage.RATE_LIMIT -> MessageType.FAILED.sendMessage(
                    player.bukkitPlayer,
                    "你的发言频率过快，请稍候再试。"
                )
            }
        })
    }

    fun sendPrivate(receiver: String, message: String) {
        val fragments: Array<FragmentMessage> = MessageFormat.parse(player, message)
        val request = PlayerPrivateChatMessage(player.coreManager.playerInfo, fragments, receiver)

        Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
            val result = Broker.invokeSync<Int>(request)
            when (result) {
                PlayerPrivateChatMessage.SUCCESS -> {}
                PlayerPrivateChatMessage.FAILURE -> MessageType.FAILED.sendMessage(
                    player.bukkitPlayer,
                    "消息发送失败。"
                )

                PlayerPrivateChatMessage.RATE_LIMIT -> MessageType.FAILED.sendMessage(
                    player.bukkitPlayer,
                    "你的发言频率过快，请稍候再试。"
                )

                PlayerPrivateChatMessage.NOT_ONLINE -> MessageType.FAILED.sendMessage(
                    player.bukkitPlayer,
                    "私聊玩家 ",
                    receiver,
                    " 不在线"
                )

                PlayerPrivateChatMessage.DENY -> MessageType.FAILED.sendMessage(
                    player.bukkitPlayer,
                    "私聊玩家 ",
                    receiver,
                    " 屏蔽了聊天消息"
                )
            }
        })
    }
}
