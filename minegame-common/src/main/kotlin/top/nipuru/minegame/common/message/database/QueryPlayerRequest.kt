package top.nipuru.minegame.common.message.database

import java.io.Serializable

class QueryPlayerRequest(val playerId: Int) : Serializable {
    val tables = ArrayList<TableInfo>()
}

class TableInfo(
    val tableName: String,
    val autoCreate: Boolean,
    val fields: Map<String, Class<*>>,
    val uniqueKeys: List<String>
) : Serializable
