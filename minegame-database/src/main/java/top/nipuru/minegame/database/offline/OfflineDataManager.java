package top.nipuru.minegame.database.offline;

import top.nipuru.minegame.common.message.PlayerOfflineDataMessage;
import top.nipuru.minegame.database.DatabaseServer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class OfflineDataManager {

    private static final String createTable = """
            CREATE TABLE IF NOT EXISTS tb_offline (
                id          BIGSERIAL    NOT NULL,
                player_id   INTEGER      NOT NULL,
                module      TEXT      NOT NULL,
                CONSTRAINT  pkey_id PRIMARY KEY (id)
            );
            """;

    private static final String createIndex =
            "CREATE INDEX IF NOT EXISTS idx_player_id ON tb_offline (player_id);";

    private static final String insert
            = "INSERT INTO tb_offline (player_id,module,data) VALUES (?,?,?);";

    private final DatabaseServer server;

    public OfflineDataManager(DatabaseServer server) {
        this.server = server;
    }

    public void init() throws SQLException {
        try (Connection con = server.getDataSourceProvider().getDataSource().getConnection()) {
            try (Statement s = con.createStatement()) {
                s.addBatch(createTable);
                s.addBatch(createIndex);
                s.executeBatch();
            }
        }
    }

    public void insert(PlayerOfflineDataMessage offlineData) throws SQLException {
        try (Connection con = server.getDataSourceProvider().getDataSource().getConnection()) {
            try (PreparedStatement ps = con.prepareStatement(insert)) {
                ps.setInt(1, offlineData.getPlayerId());
                ps.setString(2, offlineData.getModule());
                ps.setString(3, offlineData.getData());
                ps.executeUpdate();
            }
        }
    }


}
