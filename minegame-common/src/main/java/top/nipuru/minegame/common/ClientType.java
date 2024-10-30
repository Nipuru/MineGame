package top.nipuru.minegame.common;

import net.afyer.afybroker.core.BrokerClientType;

/**
 * 集群 BrokerClient 类型
 *
 * @author Nipuru
 * @since 2024/10/24 13:08
 */
public interface ClientType extends BrokerClientType {
    String DB = "db";
    String AUTH = "auth";
    String SHARED = "shared";
}
