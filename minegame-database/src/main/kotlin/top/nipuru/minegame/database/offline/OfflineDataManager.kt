package top.nipuru.minegame.database.offline

import top.nipuru.minegame.common.message.PlayerOfflineDataMessage
import top.nipuru.minegame.database.dataSource

object OfflineDataManager {

    private val createTable = """
            CREATE TABLE IF NOT EXISTS tb_offline (
                id          BIGSERIAL    NOT NULL,
                player_id   INTEGER      NOT NULL,
                module      TEXT      NOT NULL,
                CONSTRAINT  pkey_id PRIMARY KEY (id)
            );
            
            """.trimIndent()

    private const val createIndex = "CREATE INDEX IF NOT EXISTS idx_player_id ON tb_offline (player_id);"

    private const val insert = "INSERT INTO tb_offline (player_id,module,data) VALUES (?,?,?);"

    fun init() {
        dataSource.connection.use { con ->
            con.createStatement().use { s ->
                s.addBatch(createTable)
                s.addBatch(createIndex)
                s.executeBatch()
            }
        }
    }

    fun insert(offlineData: PlayerOfflineDataMessage) {
        dataSource.connection.use { con ->
            con.prepareStatement(insert).use { ps ->
                ps.setInt(1, offlineData.playerId)
                ps.setString(2, offlineData.module)
                ps.setString(3, offlineData.data)
                ps.executeUpdate()
            }
        }
    }

}
