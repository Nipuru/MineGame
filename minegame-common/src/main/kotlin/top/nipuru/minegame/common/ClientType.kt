package top.nipuru.minegame.common

import net.afyer.afybroker.core.BrokerClientType

/**
 * 集群 BrokerClient 类型
 *
 * @author Nipuru
 * @since 2024/10/24 13:08
 */
object ClientType : BrokerClientType {
    const val DB: String = "db"
    const val AUTH: String = "auth"
    const val SHARED: String = "shared"
}
