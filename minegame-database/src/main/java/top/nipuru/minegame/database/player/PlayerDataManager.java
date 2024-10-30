package top.nipuru.minegame.database.player;

import top.nipuru.minegame.common.message.database.FieldMessage;
import top.nipuru.minegame.common.message.database.PlayerTransactionRequest;
import top.nipuru.minegame.common.message.database.QueryPlayerRequest;
import top.nipuru.minegame.database.DatabaseServer;
import top.nipuru.minegame.database.util.PostgreUtils;

import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static top.nipuru.minegame.database.util.PostgreUtils.getSqlType;
import static top.nipuru.minegame.database.util.PostgreUtils.mapFieldName;

public class PlayerDataManager {

    private final Set<String> tableInitialized = ConcurrentHashMap.newKeySet();
    private final DatabaseServer server;

    public PlayerDataManager(DatabaseServer server) {
        this.server = server;
    }

    public Map<String, List<List<FieldMessage>>> queryPlayer(QueryPlayerRequest request) throws SQLException {
        Map<String, List<List<FieldMessage>>> result = new HashMap<>();
        int playerId = request.getPlayerId();
        try (Connection con = server.getDataSourceProvider().getDataSource().getConnection()) {
            for (QueryPlayerRequest.TableInfo tableInfo : request.getTables()) {
                initTable(con, tableInfo);
                String query = "select ";
                List<String> fieldNames = tableInfo.getFields().keySet().stream().map(PostgreUtils::mapFieldName).toList();
                query += String.join(",", fieldNames);
                query += " from " + tableInfo.getTableName() + " where player_id=" + playerId;

                try (ResultSet rs = con.createStatement().executeQuery(query)) {
                    List<List<FieldMessage>> lists = new ArrayList<>();
                    while (rs.next()) {
                        List<FieldMessage> fields = new ArrayList<>();
                        int i = 1;
                        for (Map.Entry<String, Class<?>> f : tableInfo.getFields().entrySet()) {
                            FieldMessage field = new FieldMessage();
                            Object object = PostgreUtils.getObject(rs, i++, f.getValue());
                            field.setName(f.getKey());
                            field.setValue(object);
                            fields.add(field);
                        }
                        lists.add(fields);
                    }
                    result.put(tableInfo.getTableName(), lists);
                }
            }
        }
        return result;
    }

    public void transaction(PlayerTransactionRequest request) throws SQLException {
        try (Connection con = server.getDataSourceProvider().getDataSource().getConnection()) {
            con.setAutoCommit(false);
            for (PlayerTransactionRequest.Delete delete : request.getDeletes()) {
                StringBuilder deleteSql = new StringBuilder();
                deleteSql.append("delete from ").append(delete.getTableName()).append(" where player_id=?");
                for (FieldMessage uniqueFields : delete.getUniqueFields()) {
                    deleteSql.append(" and ").append(mapFieldName(uniqueFields.getName())).append("=?");
                }
                try (PreparedStatement ps = con.prepareStatement(deleteSql.toString())) {
                    ps.setInt(1, request.getPlayerId());
                    for (int i = 0; i < delete.getUniqueFields().size(); ++i) {
                        PostgreUtils.setObject(con, ps, i + 2, delete.getUniqueFields().get(i).getValue());
                    }
                    ps.executeUpdate();
                }
            }

            for (PlayerTransactionRequest.Update update : request.getUpdates()) {
                StringBuilder updateSql = new StringBuilder();
                updateSql.append("update ").append(update.getTableName()).append(" set");
                for (FieldMessage updateField : update.getUpdateFields()) {
                    updateSql.append(" ").append(mapFieldName(updateField.getName())).append("=?,");
                }
                updateSql.deleteCharAt(updateSql.length() - 1);
                updateSql.append(" where player_id=?");
                for (FieldMessage uniqueFields : update.getUniqueFields()) {
                    updateSql.append(" and ").append(mapFieldName(uniqueFields.getName())).append("=?");
                }
                try (PreparedStatement ps = con.prepareStatement(updateSql.toString())) {

                    for (int i = 0; i < update.getUpdateFields().size(); ++i) {
                        PostgreUtils.setObject(con, ps, i + 1, update.getUpdateFields().get(i).getValue());
                    }
                    for (int i = 0; i < update.getUniqueFields().size(); ++i) {
                        PostgreUtils.setObject(con, ps, i + update.getUpdateFields().size() + 1, update.getUniqueFields().get(i).getValue());
                    }
                    ps.setInt(update.getUpdateFields().size() + update.getUniqueFields().size() + 1, request.getPlayerId());
                    ps.executeUpdate();
                }
            }

            for (PlayerTransactionRequest.Insert insert : request.getInserts()) {
                StringBuilder insertSql = new StringBuilder();
                insertSql.append("insert into ").append(insert.getTableName()).append("(player_id,");
                for (FieldMessage field : insert.getFields()) {
                    insertSql.append(mapFieldName(field.getName())).append(",");
                }
                insertSql.setCharAt(insertSql.length() - 1, ')');
                insertSql.append(" values(");
                insertSql.append("?,".repeat(insert.getFields().size() + 1));
                insertSql.setCharAt(insertSql.length() - 1, ')');
                try (PreparedStatement ps = con.prepareStatement(insertSql.toString())) {
                    ps.setInt(1, request.getPlayerId());
                    for (int i = 0; i < insert.getFields().size(); ++i) {
                        PostgreUtils.setObject(con, ps, i + 2, insert.getFields().get(i).getValue());
                    }
                    ps.executeUpdate();
                }
            }
            con.commit();
        }
    }

    private void initTable(Connection con, QueryPlayerRequest.TableInfo tableInfo) throws SQLException {
        if (tableInitialized.contains(tableInfo.getTableName())) return;
        StringBuilder createSql = new StringBuilder()
                .append("CREATE TABLE IF NOT EXISTS ")
                .append(tableInfo.getTableName())
                .append("(\n");
        createSql.append("    player_id INTEGER NOT NULL,\n");
        tableInfo.getFields().forEach((fieldName, fieldType) ->
                createSql.append("    ")
                        .append(mapFieldName(fieldName))
                        .append(" ")
                        .append(getSqlType(fieldType))
                        .append(" NOT NULL,\n"));
        createSql.append("    CONSTRAINT pkey_").append(tableInfo.getTableName()).append(" PRIMARY KEY (player_id");
        if (!tableInfo.getUniqueKeys().isEmpty()) {
            List<String> keys = tableInfo.getUniqueKeys().stream().map(PostgreUtils::mapFieldName).toList();
            createSql.append(",").append(String.join(",", keys));
        }
        createSql.append(")");
        createSql.append("\n);");
        try (Statement s = con.createStatement()) {
            s.execute(createSql.toString());
        }
    }
}
