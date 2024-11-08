package top.nipuru.minegame.common.message.database

import java.io.Serializable

class SaveFileRequest(val filename: String, val data: ByteArray) : Serializable
