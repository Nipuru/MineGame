package top.nipuru.minegame.server.game.chat;

import lombok.Getter;
import lombok.experimental.Accessors;
import top.nipuru.minegame.server.game.player.Data;
import top.nipuru.minegame.server.game.player.Table;
import top.nipuru.minegame.server.game.player.Temp;

@Getter
@Accessors(fluent = true)
@Table(name = "tb_chat")
public class ChatData implements Data {

    /** 禁言截止时间 */
    long mute;

    /** 私聊玩家名 */
    @Temp String msgTarget = EMPTY_STRING;
}
