package top.nipuru.minegame.auth.config;

import top.nipuru.minegame.auth.config.properties.BrokerProperties;
import top.nipuru.minegame.auth.processor.connection.ConnectionEventProcessor;
import top.nipuru.minegame.auth.util.SpringUtils;
import top.nipuru.minegame.common.ClientType;
import top.nipuru.minegame.common.processor.RequestDispatcher;
import com.alipay.remoting.LifeCycleException;
import com.alipay.remoting.exception.RemotingException;
import com.alipay.remoting.rpc.protocol.UserProcessor;
import lombok.extern.slf4j.Slf4j;
import net.afyer.afybroker.client.Broker;
import net.afyer.afybroker.client.BrokerClient;
import net.afyer.afybroker.client.BrokerClientBuilder;
import net.afyer.afybroker.core.util.BoltUtils;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Slf4j
@Configuration
public class BrokerConfig {

    @Resource
    private BrokerProperties brokerProperties;

    @PostConstruct
    public void initBrokerClient() {
        BrokerClientBuilder builder = BrokerClient.newBuilder()
                .host(brokerProperties.getHost())
                .port(brokerProperties.getPort())
                .name(ClientType.AUTH)
                .type(ClientType.AUTH);

        for (ConnectionEventProcessor processor : SpringUtils.getBeansOfType(ConnectionEventProcessor.class).values()) {
            builder.addConnectionEventProcessor(processor.type(), processor);
        }

        for (UserProcessor<?> processor : SpringUtils.getBeansOfType(UserProcessor.class).values()) {
            builder.registerUserProcessor(processor);
        }

        RequestDispatcher dispatcher = new RequestDispatcher();
        for (RequestDispatcher.Handler<?> handler : SpringUtils.getBeansOfType(RequestDispatcher.Handler.class).values()) {
            dispatcher.registerHandler(handler);
        }

        builder.registerUserProcessor(dispatcher);
        BrokerClient brokerClient = builder.build();
        Broker.setClient(brokerClient);
        try {
            BoltUtils.initProtocols();
            brokerClient.startup();
            brokerClient.ping();
        } catch (LifeCycleException e) {
            log.error("Broker client startup failed!");
            throw e;
        } catch (RemotingException | InterruptedException e) {
            log.error("Ping to the broker server failed!", e);
        }
    }
}
