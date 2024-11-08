package top.nipuru.minegame.common.message

import java.io.Serializable

class AuthServerRequest(override val request: RequestMessage) : RequestMessageContainer, Serializable
