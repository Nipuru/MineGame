package top.nipuru.minegame.database.player

import top.nipuru.minegame.common.message.database.FieldMessage
import top.nipuru.minegame.common.message.database.PlayerTransactionRequest
import top.nipuru.minegame.common.message.database.QueryPlayerRequest
import top.nipuru.minegame.common.message.database.TableInfo
import top.nipuru.minegame.database.dataSource
import top.nipuru.minegame.database.util.getObject
import top.nipuru.minegame.database.util.getSqlName
import top.nipuru.minegame.database.util.getSqlType
import top.nipuru.minegame.database.util.setObject
import java.sql.Connection
import java.sql.SQLException
import java.util.concurrent.ConcurrentHashMap

object PlayerDataManager {
    private val tableInitialized: Set<String> = ConcurrentHashMap.newKeySet()

    fun queryPlayer(request: QueryPlayerRequest): Map<String, List<List<FieldMessage>>> {
        val result: MutableMap<String, List<List<FieldMessage>>> = HashMap()
        dataSource.connection.use { con ->
            for (tableInfo in request.tables) {
                initTable(con, tableInfo)
                var query = "select "
                val fieldNames = tableInfo.fields.keys.stream()
                    .map { fieldName: String -> fieldName.getSqlName() }.toList()
                query += java.lang.String.join(",", fieldNames)
                query += " from " + tableInfo.tableName + " where player_id=" + request.playerId

                con.createStatement().executeQuery(query).use { rs ->
                    val lists: MutableList<List<FieldMessage>> = ArrayList()
                    while (rs.next()) {
                        val fields: MutableList<FieldMessage> = ArrayList()
                        var i = 1
                        for ((key, value) in tableInfo.fields) {
                            val field = FieldMessage(key, rs.getObject(value, i++))
                            fields.add(field)
                        }
                        lists.add(fields)
                    }
                    result.put(tableInfo.tableName, lists)
                }
            }
        }
        return result
    }

    fun transaction(request: PlayerTransactionRequest) {
        dataSource.connection.use { con ->
            con.autoCommit = false
            for (delete in request.deletes) {
                val deleteSql = StringBuilder()
                deleteSql.append("delete from ").append(delete.tableName).append(" where player_id=?")
                for (uniqueFields in delete.uniqueFields) {
                    deleteSql.append(" and ").append(uniqueFields.name.getSqlName()).append("=?")
                }
                con.prepareStatement(deleteSql.toString()).use { ps ->
                    ps.setInt(1, request.playerId)
                    for (i in 0 until delete.uniqueFields.count()) {
                        ps.setObject(con, i + 2, delete.uniqueFields[i].value)
                    }
                    ps.executeUpdate()
                }
            }

            for (update in request.updates) {
                val updateSql = StringBuilder()
                updateSql.append("update ").append(update.tableName).append(" set")
                for (updateField in update.updateFields) {
                    updateSql.append(" ").append(updateField.name.getSqlName()).append("=?,")
                }
                updateSql.deleteCharAt(updateSql.length - 1)
                updateSql.append(" where player_id=?")
                for (uniqueFields in update.uniqueFields) {
                    updateSql.append(" and ").append(uniqueFields.name.getSqlName()).append("=?")
                }
                con.prepareStatement(updateSql.toString()).use { ps ->
                    for (i in 0 until update.updateFields.count()) {
                        ps.setObject(con, i + 1, update.updateFields[i].value)
                    }
                    for (i in 0 until update.uniqueFields.count()) {
                        ps.setObject(con, i + update.updateFields.count() + 1, update.updateFields[i].value)
                    }
                    ps.setInt(update.updateFields.count() + update.uniqueFields.count() + 1, request.playerId)
                    ps.executeUpdate()
                }
            }

            for (insert in request.inserts) {
                val insertSql = StringBuilder()
                insertSql.append("insert into ").append(insert.tableName).append("(player_id,")
                for (field in insert.fields) {
                    insertSql.append(field.name.getSqlName()).append(",")
                }
                insertSql.setCharAt(insertSql.length - 1, ')')
                insertSql.append(" values(")
                insertSql.append("?,".repeat(insert.fields.count() + 1))
                insertSql.setCharAt(insertSql.length - 1, ')')
                con.prepareStatement(insertSql.toString()).use { ps ->
                    ps.setInt(1, request.playerId)
                    for (i in 0 until insert.fields.count()) {
                        ps.setObject(con, i + 2, insert.fields[i].value)
                    }
                    ps.executeUpdate()
                }
            }
            con.commit()
        }
    }

    @Throws(SQLException::class)
    private fun initTable(con: Connection, tableInfo: TableInfo) {
        if (tableInitialized.contains(tableInfo.tableName)) return
        val createSql = StringBuilder()
            .append("CREATE TABLE IF NOT EXISTS ")
            .append(tableInfo.tableName)
            .append("(\n")
        createSql.append("    player_id INTEGER NOT NULL,\n")
        tableInfo.fields.forEach { (fieldName: String, fieldType: Class<*>) ->
            createSql.append("    ")
                .append(fieldName.getSqlName())
                .append(" ")
                .append(fieldType.getSqlType())
                .append(" NOT NULL,\n")
        }
        createSql.append("    CONSTRAINT pkey_").append(tableInfo.tableName).append(" PRIMARY KEY (player_id")
        if (tableInfo.uniqueKeys.isNotEmpty()) {
            val keys = tableInfo.uniqueKeys.stream().map { fieldName -> fieldName.getSqlName() }
                .toList()
            createSql.append(",").append(java.lang.String.join(",", keys))
        }
        createSql.append(")")
        createSql.append("\n);")
        con.createStatement().use { s ->
            s.execute(createSql.toString())
        }
    }
}
