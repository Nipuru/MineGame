package top.nipuru.minegame.server.game.setting;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import top.nipuru.minegame.common.message.database.QueryPlayerRequest;
import top.nipuru.minegame.server.game.player.BaseManager;
import top.nipuru.minegame.server.game.player.GamePlayer;
import top.nipuru.minegame.server.game.player.DataInfo;

public class SettingManager extends BaseManager {

    private final Int2ObjectMap<SettingData> settings = new Int2ObjectOpenHashMap<>();

    public SettingManager(GamePlayer player) {
        super(player);
    }

    public static void preload(QueryPlayerRequest request) throws Exception {
        preload(request, SettingData.class);
    }

    public void unpack(DataInfo dataInfo) throws Exception {
        for (SettingData setting : dataInfo.unpackList(SettingData.class)) {
            settings.put(setting.id, setting);
        }
    }

    public void pack(DataInfo dataInfo) throws Exception {
        for (SettingData setting : settings.values()) {
            dataInfo.pack(setting);
        }
    }
}
