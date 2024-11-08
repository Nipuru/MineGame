package top.nipuru.minegame.common.message

import java.io.Serializable

class SharedServerRequest(override val request: RequestMessage) : RequestMessageContainer, Serializable
