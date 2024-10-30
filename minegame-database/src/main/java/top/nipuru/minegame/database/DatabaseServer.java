package top.nipuru.minegame.database;

import top.nipuru.minegame.common.ClientType;
import top.nipuru.minegame.common.processor.RequestDispatcher;
import top.nipuru.minegame.common.util.ResourceUtil;
import top.nipuru.minegame.database.datasource.DataSourceProvider;
import top.nipuru.minegame.database.datasource.HikariPgSQLProvider;
import top.nipuru.minegame.database.file.FileManager;
import top.nipuru.minegame.database.offline.OfflineDataManager;
import top.nipuru.minegame.database.player.PlayerDataManager;
import top.nipuru.minegame.database.processor.QueryPlayerHandler;
import top.nipuru.minegame.database.processor.PlayerOfflineDataDBProcessor;
import top.nipuru.minegame.database.processor.PlayerTransactionHandler;
import top.nipuru.minegame.database.processor.connection.CloseEventDBProcessor;
import com.alipay.remoting.ConnectionEventType;
import com.alipay.remoting.LifeCycleException;
import com.alipay.remoting.exception.RemotingException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.afyer.afybroker.client.Broker;
import net.afyer.afybroker.client.BrokerClient;
import net.afyer.afybroker.client.BrokerClientBuilder;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.io.InputStreamReader;

@Slf4j
@Getter
public class DatabaseServer {

    private DataSourceProvider dataSourceProvider;
    private BrokerClient brokerClient;
    private final PlayerDataManager playerDataManager = new PlayerDataManager(this);
    private final OfflineDataManager offlineDataManager = new OfflineDataManager(this);
    private final FileManager fileManager = new FileManager(this);

    public void startup() throws Exception {
        Config config = loadConfig();

        initDataSource(config);
        initBrokerClient(config);

        offlineDataManager.init();
    }

    public void shutdown() {
        dataSourceProvider.shutdown();
        brokerClient.shutdown();
    }

    private void buildBrokerClient(BrokerClientBuilder builder) {
        RequestDispatcher dispatcher = new RequestDispatcher();
        dispatcher.registerHandler(new QueryPlayerHandler(this));
        dispatcher.registerHandler(new PlayerTransactionHandler(this));

        builder.registerUserProcessor(dispatcher);
        builder.registerUserProcessor(new PlayerOfflineDataDBProcessor(this));
        builder.addConnectionEventProcessor(ConnectionEventType.CLOSE, new CloseEventDBProcessor());
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
            builder.name(String.format("%s-%d", ClientType.DB, config.dbId));
            builder.type(ClientType.DB);
            this.buildBrokerClient(builder);

            brokerClient = builder.build();
            Broker.setClient(brokerClient);
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
        try(InputStream inputStream = ResourceUtil.getResourceOrExtract("config.yml");
            InputStreamReader reader = new InputStreamReader(inputStream)) {
            Yaml yaml = new Yaml();
            return yaml.loadAs(reader, Config.class);
        }
    }

    public static class Config {
        public Broker broker;
        public DataSource datasource;
        public int dbId;

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
