package top.nipuru.minegame.database.datasource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class HikariPgSQLProvider implements DataSourceProvider {

    private static final String driver = "org.postgresql.Driver";
    private static final String jdbcPrefix = "jdbc:postgresql://";
    private HikariDataSource hikari;

    @Override
    public void init(String host, int port, String database, String username, String password) {
        HikariConfig config = new HikariConfig();
        config.setPoolName("DBServer-Hikari");

        String jdbcUrl = jdbcPrefix + host + ":" + port + "/" + database;
        config.setJdbcUrl(jdbcUrl);
        config.setDriverClassName(driver);
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(10);
        config.setMaxLifetime(1800000);
        config.setConnectionTimeout(5000);

        this.hikari = new HikariDataSource(config);
    }

    @Override
    public void shutdown() {
        if (hikari != null) hikari.close();
    }

    @Override
    public DataSource getDataSource() {
        return hikari;
    }
}
