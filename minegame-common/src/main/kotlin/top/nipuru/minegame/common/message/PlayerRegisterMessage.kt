package top.nipuru.minegame.common.message

import java.io.Serializable
import java.util.*

class PlayerRegisterMessage(val uniqueId: UUID, val playerId: Int) : Serializable
