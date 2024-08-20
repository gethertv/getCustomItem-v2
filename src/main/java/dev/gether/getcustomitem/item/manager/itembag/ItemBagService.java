package dev.gether.getcustomitem.item.manager.itembag;

import dev.gether.getconfig.utils.ConsoleColor;
import dev.gether.getconfig.utils.MessageUtil;
import dev.gether.getcustomitem.file.FileManager;
import dev.gether.getcustomitem.storage.DatabaseType;
import dev.gether.getcustomitem.storage.MySQL;

import java.sql.*;
import java.util.*;

public class ItemBagService {

    private static final int BATCH_SIZE = 1000;
    private final String table = "backpack";
    private final MySQL mySQL;
    private final FileManager fileManager;
    private final DatabaseType databaseType;

    public ItemBagService(MySQL mySQL, FileManager fileManager) {
        this.mySQL = mySQL;
        this.fileManager = fileManager;
        this.databaseType = fileManager.getDatabaseConfig().getDatabaseType();
        createTable();
    }

    private void createTable() {
        String query = "CREATE TABLE IF NOT EXISTS " + table + " ("
                + "uuid VARCHAR(36) PRIMARY KEY,"
                + "inventory TEXT,"
                + "`key` VARCHAR(20))";

        try (Connection conn = mySQL.getHikariDataSource().getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(query);
        } catch (SQLException e) {
            MessageUtil.logMessage(ConsoleColor.RED, "[getCustomItem] Cannot create the table " + table + ". Error " + e.getMessage());
        }
    }

    public void batchUpdateInventories(Map<UUID, ItemBagDTO> inventories) {
        String updateQuery;
        if (databaseType == DatabaseType.MYSQL) {
            updateQuery = "INSERT INTO " + table + " (uuid, inventory, `key`) VALUES (?, ?, ?) "
                    + "ON DUPLICATE KEY UPDATE inventory = VALUES(inventory)";
        } else {
            updateQuery = "INSERT OR REPLACE INTO " + table + " (uuid, inventory, `key`) VALUES (?, ?, ?)";
        }

        try (Connection conn = mySQL.getHikariDataSource().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {

            conn.setAutoCommit(false);

            int count = 0;
            for (Map.Entry<UUID, ItemBagDTO> entry : inventories.entrySet()) {
                pstmt.setString(1, entry.getKey().toString());
                pstmt.setString(2, entry.getValue().inventory());
                pstmt.setString(3, entry.getValue().key());
                pstmt.addBatch();

                if (++count % BATCH_SIZE == 0) {
                    pstmt.executeBatch();
                    conn.commit();
                    pstmt.clearBatch();
                }
            }

            // Execute any remaining records
            if (count % BATCH_SIZE != 0) {
                pstmt.executeBatch();
                conn.commit();
            }

        } catch (SQLException e) {
            MessageUtil.logMessage(ConsoleColor.RED, "[getCustomItem] Error during batch update of inventories: " + e.getMessage());
        }
    }

    public Map<UUID, ItemBagDTO> loadAllInventories() {
        Map<UUID, ItemBagDTO> inventories = new HashMap<>();
        String selectQuery = "SELECT uuid, inventory, `key` FROM " + table;

        try (Connection conn = mySQL.getHikariDataSource().getConnection();
             Statement stmt = conn.createStatement()) {

            if (databaseType == DatabaseType.MYSQL) {
                stmt.setFetchSize(BATCH_SIZE);
            }
            try (ResultSet rs = stmt.executeQuery(selectQuery)) {
                while (rs.next()) {
                    UUID uuid = UUID.fromString(rs.getString("uuid"));
                    String inventory = rs.getString("inventory");
                    String key = rs.getString("key");
                    inventories.put(uuid, new ItemBagDTO(inventory, key));
                }
            }

        } catch (SQLException e) {
            MessageUtil.logMessage(ConsoleColor.RED, "[getCustomItem] Error loading inventories: " + e.getMessage());
        }

        return inventories;
    }
}