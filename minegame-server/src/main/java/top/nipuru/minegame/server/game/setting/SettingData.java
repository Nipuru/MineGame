package top.nipuru.minegame.server.game.setting;

import top.nipuru.minegame.server.game.player.Data;
import top.nipuru.minegame.server.game.player.Table;
import top.nipuru.minegame.server.game.player.Unique;

@Table(name = "tb_setting")
public class SettingData implements Data {

    /** 设置 id */
    @Unique int id;

    /** 选项值 */
    int option;
}
