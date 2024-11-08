package top.nipuru.minegame.auth.user

import lombok.Data
import java.util.*


/**
 * @author Nipuru
 * @since 2024/11/07 17:38
 */
class User {
    val playerId = 0
    val uniqueId: UUID = UUID.randomUUID()
    val lastIp: String = ""
    val dbId = 0
    val createTime: Long = 0
}
