package org.nocraft.loperd.playerdatasync.common.storage;

import org.nocraft.loperd.playerdatasync.common.plugin.PDSyncPlugin;
import org.nocraft.loperd.playerdatasync.common.config.ConfigKeys;
import org.nocraft.loperd.playerdatasync.common.storage.implementation.SqlStorage;
import org.nocraft.loperd.playerdatasync.common.storage.implementation.StorageImplementation;
import org.nocraft.loperd.playerdatasync.common.storage.implementation.custom.CustomStorageProviders;
import org.nocraft.loperd.playerdatasync.common.storage.implementation.sql.connection.hikari.MariaDbConnectionFactory;
import org.nocraft.loperd.playerdatasync.common.storage.implementation.sql.connection.hikari.MySqlConnectionFactory;
import org.nocraft.loperd.playerdatasync.common.storage.implementation.sql.connection.hikari.PostgreConnectionFactory;

public class StorageFactory {

    private final PDSyncPlugin plugin;

    public StorageFactory(PDSyncPlugin plugin) {
        this.plugin = plugin;
    }

    public Storage getInstance() {
        StorageType type = this.plugin.getConfiguration().get(ConfigKeys.STORAGE_METHOD);
        this.plugin.getLogger().info("Loading storage provider... [" + type.name() + "]");
        Storage storage = new Storage(this.plugin, createNewImplementation(type));

        storage.init();
        return storage;
    }

    private StorageImplementation createNewImplementation(StorageType method) {
        switch (method) {
            case CUSTOM:
                return CustomStorageProviders.getProvider().provide(this.plugin);
            case MARIADB:
                return new SqlStorage(
                        this.plugin,
                        new MariaDbConnectionFactory(this.plugin.getConfiguration().get(ConfigKeys.DATABASE_VALUES)),
                        this.plugin.getConfiguration().get(ConfigKeys.SQL_TABLE_PREFIX)
                );
            case MYSQL:
                return new SqlStorage(
                        this.plugin,
                        new MySqlConnectionFactory(this.plugin.getConfiguration().get(ConfigKeys.DATABASE_VALUES)),
                        this.plugin.getConfiguration().get(ConfigKeys.SQL_TABLE_PREFIX)
                );
            case POSTGRESQL:
                return new SqlStorage(
                        this.plugin,
                        new PostgreConnectionFactory(this.plugin.getConfiguration().get(ConfigKeys.DATABASE_VALUES)),
                        this.plugin.getConfiguration().get(ConfigKeys.SQL_TABLE_PREFIX)
                );
            default:
                throw new RuntimeException("Unknown method: " + method);
        }
    }
}
