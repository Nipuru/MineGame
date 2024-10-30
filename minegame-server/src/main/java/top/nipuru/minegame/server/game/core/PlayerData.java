package top.nipuru.minegame.server.game.core;

import lombok.Getter;
import lombok.experimental.Accessors;
import top.nipuru.minegame.server.game.player.Data;
import top.nipuru.minegame.server.game.player.Table;

@Getter
@Accessors(fluent = true)
@Table(name = "tb_player")
public class PlayerData implements Data {

    /** 货币 */
    long coin;

    /** 点券 */
    long points;

    /** 头衔id */
    int rankId;

    /** 徽章id */
    int medalId;

    /** 创建时间 */
    long createTime;

    /** 最后离线时间 */
    long lastLogoutTime;

    /** 重置时间 */
    long resetTime;

    /** 累计在线时间 */
    long playedTime;

    /** 生日 birthday[0]:月,birthday[1]:日 */
    int[] birthday = EMPTY_INT_ARRAY;
}
