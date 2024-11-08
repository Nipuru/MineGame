package top.nipuru.minegame.common.message.database

import java.io.Serializable


class PlayerTransactionRequest(val playerId: Int) : Serializable {
    val deletes = ArrayList<Delete>()
    val updates = ArrayList<Update>()
    val inserts = ArrayList<Insert>()
}

class Delete(val tableName: String, val uniqueFields: List<FieldMessage>) : Serializable

class Insert(val tableName: String, val fields: List<FieldMessage>) : Serializable

class Update(val tableName: String, val uniqueFields: List<FieldMessage>, val updateFields: List<FieldMessage>) : Serializable