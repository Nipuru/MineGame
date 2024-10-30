package top.nipuru.minegame.database.datasource;

import javax.sql.DataSource;

public interface DataSourceProvider {

    void init(String host, int port, String database, String username, String password) throws Exception;

    void shutdown();

    DataSource getDataSource();
}
