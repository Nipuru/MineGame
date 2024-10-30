package top.nipuru.minegame.shared.player;

import top.nipuru.minegame.common.message.shared.PlayerInfoMessage;
import top.nipuru.minegame.shared.SharedServer;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerInfoManager {

    private static final String createTable = """
            CREATE TABLE IF NOT EXISTS tb_player_info(
                player_id        INTEGER     NOT NULL,
                name             VARCHAR(16) NOT NULL,
                coin             BIGINT      NOT NULL,
                db_id            INTEGER     NOT NULL,
                rank_id          INTEGER     NOT NULL,
                create_time      BIGINT      NOT NULL,
                last_logout_time BIGINT      NOT NULL,
                played_time      BIGINT      NOT NULL,
                CONSTRAINT pkey_player_id    PRIMARY KEY (player_id),
                CONSTRAINT uni_name          UNIQUE (name)
              );
            """;

    private static final String selectById
            = "SELECT player_id,name,coin,db_id,rank_id,create_time,last_logout_time,played_time from tb_player_info WHERE player_id = ?;";

    private static final String selectByName
            = "SELECT player_id,name,coin,db_id,rank_id,create_time,last_logout_time,played_time from tb_player_info WHERE name = ?;";

    private static final String insertOfUpdate
            = "INSERT INTO tb_player_info(player_id,name,coin,db_id,rank_id,photo_frame_id,profile_bg_id,create_time,last_logout_time,played_time) " +
            "VALUES (?,?,?,?,?,?,?,?,?,?) " +
            "ON CONFLICT (player_id) DO UPDATE SET " +
            "name=EXCLUDED.name, " +
            "coin=EXCLUDED.coin, " +
            "db_id=EXCLUDED.db_id, " +
            "rank_id=EXCLUDED.rank_id, " +
            "create_time=EXCLUDED.create_time, " +
            "last_logout_time=EXCLUDED.last_logout_time, " +
            "played_time=EXCLUDED.played_time ";



    private final Map<Integer, PlayerInfoMessage> byId = new ConcurrentHashMap<>();
    private final Map<String, PlayerInfoMessage> byName = new ConcurrentHashMap<>();
    private final SharedServer server;

    public PlayerInfoManager(SharedServer server) {
        this.server = server;
    }

    public void init() throws SQLException {
        try (Connection conn = server.getDataSourceProvider().getDataSource().getConnection()) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(createTable);
            }
        }
    }

    @Nullable
    public PlayerInfoMessage getById(int playerId) throws SQLException {
        PlayerInfoMessage playerInfo = byId.get(playerId);
        if (playerInfo != null) {
            return playerInfo;
        }

        // 查数据库
        try (Connection conn = server.getDataSourceProvider().getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(selectById)) {
            ps.setInt(1, playerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    playerInfo = new PlayerInfoMessage()
                            .setPlayerId(rs.getInt(1))
                            .setName(rs.getString(2))
                            .setCoin(rs.getInt(3))
                            .setDbId(rs.getInt(4))
                            .setRankId(rs.getInt(5))
                            .setCreateTime(rs.getLong(6))
                            .setLastLogoutTime(rs.getLong(7))
                            .setPlayedTime(rs.getLong(8));
                    byId.put(playerInfo.getPlayerId(), playerInfo);
                    byName.put(playerInfo.getName(), playerInfo);
                }
            }
        }
        return playerInfo;
    }

    @Nullable
    public PlayerInfoMessage getByName(String name) throws SQLException {
        PlayerInfoMessage playerInfo = byName.get(name);
        if (playerInfo != null) {
            return playerInfo;
        }

        // 查数据库
        try (Connection conn = server.getDataSourceProvider().getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(selectByName)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    playerInfo = new PlayerInfoMessage()
                            .setPlayerId(rs.getInt(1))
                            .setName(rs.getString(2))
                            .setCoin(rs.getInt(3))
                            .setDbId(rs.getInt(4))
                            .setRankId(rs.getInt(5))
                            .setCreateTime(rs.getLong(6))
                            .setLastLogoutTime(rs.getLong(7))
                            .setPlayedTime(rs.getLong(8));
                    byId.put(playerInfo.getPlayerId(), playerInfo);
                    byName.put(playerInfo.getName(), playerInfo);
                }
            }
        }
        return playerInfo;
    }

    public void insertOrUpdate(PlayerInfoMessage playerInfo) throws SQLException {
        // 更新数据库
        try (Connection conn = server.getDataSourceProvider().getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(insertOfUpdate)) {
            ps.setInt(1, playerInfo.getPlayerId());
            ps.setString(2, playerInfo.getName());
            ps.setLong(3, playerInfo.getCoin());
            ps.setInt(4, playerInfo.getDbId());
            ps.setInt(5, playerInfo.getRankId());
            ps.setLong(6, playerInfo.getCreateTime());
            ps.setLong(7, playerInfo.getLastLogoutTime());
            ps.setLong(8, playerInfo.getPlayedTime());
            ps.executeUpdate();
        }
        PlayerInfoMessage remove = byId.remove(playerInfo.getPlayerId());
        if (remove != null) {
            byName.remove(playerInfo.getName());
        }
    }


}
