package top.nipuru.minegame.server.game.chat;

import com.alipay.remoting.exception.RemotingException;
import lombok.extern.slf4j.Slf4j;
import top.nipuru.minegame.common.message.FragmentMessage;
import top.nipuru.minegame.common.message.PlayerChatMessage;
import top.nipuru.minegame.common.message.PlayerPrivateChatMessage;
import top.nipuru.minegame.common.message.database.QueryPlayerRequest;
import top.nipuru.minegame.common.message.shared.PlayerInfoMessage;
import top.nipuru.minegame.server.MessageType;
import top.nipuru.minegame.server.TimeMgr;
import top.nipuru.minegame.server.game.player.BaseManager;
import top.nipuru.minegame.server.game.player.GamePlayer;
import top.nipuru.minegame.server.game.player.DataInfo;
import net.afyer.afybroker.client.Broker;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Slf4j
public class ChatManager extends BaseManager {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-dd-MM HH:mm:ss");

    private ChatData data;

    public ChatManager(GamePlayer player) {
        super(player);
    }

    public static void preload(QueryPlayerRequest request) throws Exception {
        preload(request, ChatData.class);
    }

    public void unpack(DataInfo dataInfo) throws Exception {
        data = dataInfo.unpack(ChatData.class);
        if (data == null) {
            data = new ChatData();
            player.insert(data);
        }
    }

    public void pack(DataInfo dataInfo) throws Exception {
        dataInfo.pack(data);
    }

    public String getMsgTarget() {
        return data.msgTarget;
    }

    public boolean hasMsgTarget() {
        return !data.msgTarget.isEmpty();
    }

    public void setMsgTarget(String msgTarget) {
        data.msgTarget = msgTarget;
    }

    public void clearMsgTarget() {
        data.msgTarget = ChatData.EMPTY_STRING;
    }

    public long getMute() {
        return data.mute;
    }

    public boolean isMuted() {
        return data.mute > TimeMgr.now();
    }

    public void setMute(long time) {
        data.mute = time;
        player.update(data, ChatData::mute);
    }

    public void unmute() {
        long now = TimeMgr.now();
        if (data.mute > now) return;
        data.mute = now;
        player.update(data, ChatData::mute);
    }

    public LocalDateTime getMuteDateTime() {
        LocalDateTime dateTime = Instant.ofEpochMilli(data.mute)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        return dateTime;
    }

    public FragmentMessage[] parseMessage(String message) {
        return MessageFormat.parse(player, message);
    }

    public boolean couldReceivePrivate(PlayerInfoMessage sender) {
        return true;
    }

    public void receivePublic(PlayerInfoMessage sender, FragmentMessage[] fragments) {
        TextComponent.Builder builder = Component.text();
        builder.color(NamedTextColor.WHITE);
        builder.append(Component.text(sender.getName() + ": "));
        builder.append(MessageFormat.format(sender, player, fragments));
        player.getBukkitPlayer().sendMessage(builder.build());
    }

    public void receivePrivate(PlayerInfoMessage sender, FragmentMessage[] fragments) {
        TextComponent.Builder builder = Component.text();
        builder.color(NamedTextColor.WHITE);
        builder.append(Component.text(sender.getName() + " 对你说: "));
        builder.append(MessageFormat.format(sender, player, fragments));
        player.getBukkitPlayer().sendMessage(builder.build());
    }

    public void sendPublic(String message) {
        FragmentMessage[] fragments = MessageFormat.parse(player, message);
        PlayerChatMessage request = new PlayerChatMessage()
                .setSender(player.getCoreManager().getPlayerInfoMessage())
                .setFragments(fragments);

        Bukkit.getScheduler().runTaskAsynchronously(player.getPlugin(), () -> {
            try {
                int result = Broker.invokeSync(request);
                switch (result) {
                    case PlayerChatMessage.SUCCESS:
                        // do nothing
                        break;
                    case PlayerChatMessage.FAILURE:
                        MessageType.FAILED.sendMessage(player.getBukkitPlayer(), "消息发送失败。");
                        break;
                    case PlayerChatMessage.RATE_LIMIT:
                        MessageType.FAILED.sendMessage(player.getBukkitPlayer(), "你的发言频率过快，请稍候再试。");
                        break;
                }
            } catch (RemotingException | InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        });
    }

    public void sendPrivate(String receiver, String message) {
        FragmentMessage[] fragments = MessageFormat.parse(player, message);
        PlayerPrivateChatMessage request = new PlayerPrivateChatMessage()
                .setSender(player.getCoreManager().getPlayerInfoMessage())
                .setFragments(fragments)
                .setReceiver(receiver);

        Bukkit.getScheduler().runTaskAsynchronously(player.getPlugin(), () -> {
            try {
                int result = Broker.invokeSync(request);
                switch (result) {
                    case PlayerPrivateChatMessage.SUCCESS:
                        // do nothing
                        break;
                    case PlayerPrivateChatMessage.FAILURE:
                        MessageType.FAILED.sendMessage(player.getBukkitPlayer(), "消息发送失败。");
                        break;
                    case PlayerPrivateChatMessage.RATE_LIMIT:
                        MessageType.FAILED.sendMessage(player.getBukkitPlayer(), "你的发言频率过快，请稍候再试。");
                        break;
                    case PlayerPrivateChatMessage.NOT_ONLINE:
                        MessageType.FAILED.sendMessage(player.getBukkitPlayer(), "私聊玩家 ", receiver, " 不在线");
                        break;
                    case PlayerPrivateChatMessage.DENY:
                        MessageType.FAILED.sendMessage(player.getBukkitPlayer(), "私聊玩家 ", receiver, " 屏蔽了聊天消息");
                        break;
                }
            } catch (RemotingException | InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        });
    }
}
