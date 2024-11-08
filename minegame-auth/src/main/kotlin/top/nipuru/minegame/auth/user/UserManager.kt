package top.nipuru.minegame.auth.user

import top.nipuru.minegame.auth.dataSource
import java.util.*


/**
 * @author Nipuru
 * @since 2024/11/07 17:28
 */
object UserManager {

    private val createTable = """
            CREATE TABLE IF NOT EXISTS tb_user (
                player_id   SERIAL       NOT NULL,
                unique_id   VARCHAR(36)  NOT NULL,
                last_ip     TEXT         NOT NULL,
                db_id       INTEGER      NOT NULL,
                create_time BIGINT       NOT NULL,
                CONSTRAINT  pkey_tb_user PRIMARY KEY (player_id)
                CONSTRAINT  uni_tb_user  UNIQUE KEY (unique_id)
            );
            """.trimIndent()

    private const val select = "SELECT player_id,db_id FROM tb_user where unique_id=?;"

    private const val insert = "INSERT INTO tb_user (player_id, unique_id, db_id, create_time) VALUES (?, ?, ?, ?)"

    private const val update = "UPDATE tb_user set last_ip = ? WHERE player_id = ?"

    fun init() {
        dataSource.connection.use { conn ->
            conn.createStatement().use { stmt ->
                stmt.execute(
                    createTable
                )
            }
        }
        println()
    }


    fun initUser(uniqueId: UUID, lastIp: String): User{
        TODO()
    }

}
