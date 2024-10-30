package top.nipuru.minegame.auth.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Nipuru
 * @since 2024/03/09 17:21
 */
@Data
@Component
@ConfigurationProperties(prefix = "broker")
public class BrokerProperties {

    /**
     * 地址
     */
    private String host;

    /**
     * 端口
     */
    private int port;
}
