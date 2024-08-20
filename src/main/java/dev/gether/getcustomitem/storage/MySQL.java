package dev.gether.getcustomitem.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.gether.getcustomitem.GetCustomItem;
import dev.gether.getcustomitem.file.FileManager;
import dev.gether.getcustomitem.file.config.DatabaseConfig;
import org.bukkit.plugin.java.JavaPlugin;

public class MySQL {

    private final GetCustomItem plugin;
    private FileManager fileManager;

    private HikariDataSource hikariDataSource;

    public MySQL(GetCustomItem plugin, FileManager fileManager) {
        this.plugin = plugin;
        this.fileManager = fileManager;
        connect(plugin);
    }

    private void connect(JavaPlugin plugin) {
        DatabaseConfig databaseConfig = fileManager.getDatabaseConfig();
        HikariConfig config = new HikariConfig();
        if(databaseConfig.getDatabaseType() == DatabaseType.MYSQL) {
            config.setJdbcUrl("jdbc:mysql://" + databaseConfig.getHost() + ":" + databaseConfig.getPort() + "/" + databaseConfig.getDatabase());
            config.setUsername(databaseConfig.getUsername());
            config.setPassword(databaseConfig.getPassword());
        } else {
            config.setJdbcUrl("jdbc:sqlite:"+plugin.getDataFolder() + "/database.db");
        }
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        this.hikariDataSource = new HikariDataSource(config);
    }

    public void disconnect() {
        if(this.hikariDataSource != null) {
            hikariDataSource.close();
        }
    }

    public HikariDataSource getHikariDataSource() {
        return hikariDataSource;
    }

    public boolean isConnected() {
        return (hikariDataSource != null && !hikariDataSource.isClosed());
    }


}
