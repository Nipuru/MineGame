package top.nipuru.minegame.shared.player

import top.nipuru.minegame.common.message.shared.PlayerInfoMessage
import top.nipuru.minegame.shared.SharedServer
import top.nipuru.minegame.shared.dataSource
import java.sql.SQLException
import java.util.concurrent.ConcurrentHashMap

object PlayerInfoManager {
    private val createTable = """
            CREATE TABLE IF NOT EXISTS tb_player_info(
                player_id        INTEGER     NOT NULL,
                name             VARCHAR(16) NOT NULL,
                coin             BIGINT      NOT NULL,
                db_id            INTEGER     NOT NULL,
                rank_id          INTEGER     NOT NULL,
                create_time      BIGINT      NOT NULL,
                last_logout_time BIGINT      NOT NULL,
                played_time      BIGINT      NOT NULL,
                CONSTRAINT pkey_player_id    PRIMARY KEY (player_id),
                CONSTRAINT uni_name          UNIQUE (name)
              );
            
            """.trimIndent()

    private const val selectById =
        "SELECT player_id,name,coin,db_id,rank_id,create_time,last_logout_time,played_time from tb_player_info WHERE player_id = ?;"

    private const val selectByName =
        "SELECT player_id,name,coin,db_id,rank_id,create_time,last_logout_time,played_time from tb_player_info WHERE name = ?;"

    private const val insertOfUpdate =
        "INSERT INTO tb_player_info(player_id,name,coin,db_id,rank_id,photo_frame_id,profile_bg_id,create_time,last_logout_time,played_time) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?) " +
                "ON CONFLICT (player_id) DO UPDATE SET " +
                "name=EXCLUDED.name, " +
                "coin=EXCLUDED.coin, " +
                "db_id=EXCLUDED.db_id, " +
                "rank_id=EXCLUDED.rank_id, " +
                "create_time=EXCLUDED.create_time, " +
                "last_logout_time=EXCLUDED.last_logout_time, " +
                "played_time=EXCLUDED.played_time "

    private val byId: MutableMap<Int, PlayerInfoMessage?> = ConcurrentHashMap()
    private val byName: MutableMap<String, PlayerInfoMessage?> = ConcurrentHashMap()

    fun init() {
        dataSource.connection.use { conn ->
            conn.createStatement().use { stmt ->
                stmt.execute(
                    createTable
                )
            }
        }
    }

    fun getById(playerId: Int): PlayerInfoMessage? {
        var playerInfo = byId[playerId]
        if (playerInfo != null) {
            return playerInfo
        }

        dataSource.connection.use { conn ->
            conn.prepareStatement(selectById).use { ps ->
                ps.setInt(1, playerId)
                ps.executeQuery().use { rs ->
                    if (rs.next()) {
                        playerInfo = PlayerInfoMessage().also {
                            it.playerId = rs.getInt(1)
                            it.name = rs.getString(2)
                            it.coin = rs.getLong(3)
                            it.dbId = rs.getInt(4)
                            it.rankId = rs.getInt(5)
                            it.createTime = rs.getLong(6)
                            it.lastLogoutTime = rs.getLong(7)
                            it.playedTime = rs.getLong(8)
                        }
                        byId[playerInfo!!.playerId] = playerInfo
                        byName[playerInfo!!.name] = playerInfo
                    }
                }
            }
        }
        return playerInfo
    }


    fun getByName(name: String): PlayerInfoMessage? {
        var playerInfo = byName[name]
        if (playerInfo != null) {
            return playerInfo
        }

        dataSource.connection.use { conn ->
            conn.prepareStatement(selectByName).use { ps ->
                ps.setString(1, name)
                ps.executeQuery().use { rs ->
                    if (rs.next()) {
                        playerInfo = PlayerInfoMessage().also {
                            it.playerId = rs.getInt(1)
                            it.name = rs.getString(2)
                            it.coin = rs.getLong(3)
                            it.dbId = rs.getInt(4)
                            it.rankId = rs.getInt(5)
                            it.createTime = rs.getLong(6)
                            it.lastLogoutTime = rs.getLong(7)
                            it.playedTime = rs.getLong(8)
                        }
                        byId[playerInfo!!.playerId] = playerInfo
                        byName[playerInfo!!.name] = playerInfo
                    }
                }
            }
        }
        return playerInfo
    }

    fun insertOrUpdate(playerInfo: PlayerInfoMessage) {
        // 更新数据库
        dataSource.connection.use { conn ->
            conn.prepareStatement(insertOfUpdate).use { ps ->
                ps.setInt(1, playerInfo.playerId)
                ps.setString(2, playerInfo.name)
                ps.setLong(3, playerInfo.coin)
                ps.setInt(4, playerInfo.dbId)
                ps.setInt(5, playerInfo.rankId)
                ps.setLong(6, playerInfo.createTime)
                ps.setLong(7, playerInfo.lastLogoutTime)
                ps.setLong(8, playerInfo.playedTime)
                ps.executeUpdate()
            }
        }
        val remove = byId.remove(playerInfo.playerId)
        if (remove != null) {
            byName.remove(playerInfo.name)
        }
    }
}
