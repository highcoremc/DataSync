package org.nocraft.loperd.playerdatasync.Storage;

import org.nocraft.loperd.playerdatasync.Domain.Config.ConfigKeys;
import org.nocraft.loperd.playerdatasync.NoPlayerDataSync;
import org.nocraft.loperd.playerdatasync.Storage.implementation.StorageImplementation;
import org.nocraft.loperd.playerdatasync.Storage.implementation.custom.CustomStorageProviders;
import org.nocraft.loperd.playerdatasync.Storage.implementation.sql.SqlStorage;
import org.nocraft.loperd.playerdatasync.Storage.implementation.sql.connection.hikari.MariaDbConnectionFactory;
import org.nocraft.loperd.playerdatasync.Storage.implementation.sql.connection.hikari.MySqlConnectionFactory;
import org.nocraft.loperd.playerdatasync.Storage.implementation.sql.connection.hikari.PostgreConnectionFactory;
import org.nocraft.loperd.playerdatasync.Storage.misc.StorageCredentials;

public class StorageFactory {

    private final NoPlayerDataSync plugin;

    public StorageFactory(NoPlayerDataSync plugin) {
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
