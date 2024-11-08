package top.nipuru.minegame.common.message

import java.io.Serializable

class PlayerOfflineDataMessage(val playerId: Int, val dbId: Int, val module: String, val data: String) : Serializable
