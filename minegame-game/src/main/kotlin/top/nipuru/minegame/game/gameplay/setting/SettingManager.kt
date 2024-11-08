package top.nipuru.minegame.game.gameplay.setting

import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import top.nipuru.minegame.common.message.database.QueryPlayerRequest
import top.nipuru.minegame.game.gameplay.player.BaseManager
import top.nipuru.minegame.game.gameplay.player.DataInfo
import top.nipuru.minegame.game.gameplay.player.GamePlayer
import top.nipuru.minegame.game.gameplay.player.preload

class SettingManager(player: GamePlayer) : BaseManager(player) {

    private val settings: Int2ObjectMap<SettingData> = Int2ObjectOpenHashMap()

    fun preload(request: QueryPlayerRequest) {
        request.preload(SettingData::class.java)
    }

    fun unpack(dataInfo: DataInfo) {
        for (setting in dataInfo.unpackList(SettingData::class.java)) {
            settings.put(setting.id, setting)
        }
    }

    fun pack(dataInfo: DataInfo) {
        for (setting in settings.values) {
            dataInfo.pack(setting)
        }
    }
}
