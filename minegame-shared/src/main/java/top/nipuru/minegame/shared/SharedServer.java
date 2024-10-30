package top.nipuru.minegame.shared;

import top.nipuru.minegame.common.ClientType;
import top.nipuru.minegame.common.processor.RequestDispatcher;
import top.nipuru.minegame.common.util.ResourceUtil;
import top.nipuru.minegame.shared.auction.ListingManager;
import top.nipuru.minegame.shared.datasource.DataSourceProvider;
import top.nipuru.minegame.shared.datasource.HikariPgSQLProvider;
import top.nipuru.minegame.shared.player.PlayerInfoManager;
import top.nipuru.minegame.shared.processor.PlayerInfoUpdateHandler;
import top.nipuru.minegame.shared.processor.connection.CloseEventSharedProcessor;
import com.alipay.remoting.ConnectionEventType;
import com.alipay.remoting.LifeCycleException;
import com.alipay.remoting.exception.RemotingException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.afyer.afybroker.client.Broker;
import net.afyer.afybroker.client.BrokerClient;
import net.afyer.afybroker.client.BrokerClientBuilder;
import net.afyer.afybroker.core.util.BoltUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.io.InputStreamReader;

@Slf4j
@Getter
public class SharedServer {

    private BrokerClient brokerClient;
    private DataSourceProvider dataSourceProvider;
    private final ListingManager listingManager = new ListingManager(this);
    private final PlayerInfoManager playerInfoManager = new PlayerInfoManager(this);

    public void startup() throws Exception {
        Config config = loadConfig();

        initDataSource(config);
        initBrokerClient(config);

        listingManager.init();
        playerInfoManager.init();
    }

    public void shutdown() {
        brokerClient.shutdown();
    }

    private void buildBrokerClient(BrokerClientBuilder builder) {
        RequestDispatcher dispatcher = new RequestDispatcher();
        dispatcher.registerHandler(new PlayerInfoUpdateHandler(this));
        dispatcher.registerHandler(new PlayerInfoUpdateHandler(this));

        builder.registerUserProcessor(dispatcher);
        builder.addConnectionEventProcessor(ConnectionEventType.CLOSE, new CloseEventSharedProcessor());
    }

    private void initDataSource(Config config) throws Exception {
        dataSourceProvider = new HikariPgSQLProvider();
        Config.DataSource datasource = config.datasource;
        dataSourceProvider.init(datasource.host, datasource.port, datasource.database, datasource.username, datasource.password);
    }

    private void initBrokerClient(Config config) throws Exception {
        try {
            BrokerClientBuilder builder = BrokerClient.newBuilder();
            builder.host(config.broker.host);
            builder.port(config.broker.port);
            builder.name(ClientType.SHARED);
            builder.type(ClientType.SHARED);
            this.buildBrokerClient(builder);

            brokerClient = builder.build();
            Broker.setClient(brokerClient);
            BoltUtils.initProtocols();
            brokerClient.startup();
            brokerClient.ping();
        } catch (LifeCycleException e) {
            log.error("Broker client startup failed!");
            throw e;
        } catch (RemotingException | InterruptedException e) {
            log.error("Ping to the broker server failed!");
            throw e;
        }
    }

    private static Config loadConfig() throws Exception {
        try (InputStream inputStream = ResourceUtil.getResourceOrExtract("config.yml");
             InputStreamReader reader = new InputStreamReader(inputStream)) {
            Yaml yaml = new Yaml();
            return yaml.loadAs(reader, Config.class);
        }
    }

    public static class Config {
        public Broker broker;
        public DataSource datasource;

        public static class Broker {
            public String host;
            public int port;
        }

        public static class DataSource {
            public String host;
            public int port;
            public String database;
            public String username;
            public String password;
        }
    }
}
