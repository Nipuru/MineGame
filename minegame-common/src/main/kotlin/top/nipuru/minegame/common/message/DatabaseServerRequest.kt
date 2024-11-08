package top.nipuru.minegame.common.message

import java.io.Serializable

class DatabaseServerRequest(val dbId: Int, override val request: RequestMessage) : RequestMessageContainer, Serializable
