package org.nocraft.loperd.playerdatasync.Domain.Config;

import com.google.common.collect.ImmutableMap;
import org.nocraft.loperd.playerdatasync.Domain.ImmutableCollectors;
import org.nocraft.loperd.playerdatasync.Storage.StorageType;
import org.nocraft.loperd.playerdatasync.Storage.misc.StorageCredentials;

import java.lang.reflect.Modifier;
import java.util.*;

import static org.nocraft.loperd.playerdatasync.Domain.Config.ConfigKeyTypes.*;

/**
 * All of the {@link ConfigKey}s.
 *
 * <p>The {@link #getKeys()} method and associated behaviour allows this class
 * to function a bit like an enum, but with generics.</p>
 */
public final class ConfigKeys {

    private ConfigKeys() {}

    /**
     * The database settings, username, password, etc for use by any database
     */
    public static final ConfigKey<StorageCredentials> DATABASE_VALUES = enduringKey(customKey(c -> {
        int maxPoolSize = c.getInteger("data.pool-settings.maximum-pool-size", c.getInteger("data.pool-size", 10));
        int minIdle = c.getInteger("data.pool-settings.minimum-idle", maxPoolSize);
        int maxLifetime = c.getInteger("data.pool-settings.maximum-lifetime", 1800000);
        int connectionTimeout = c.getInteger("data.pool-settings.connection-timeout", 5000);
        Map<String, String> props = ImmutableMap.copyOf(c.getStringMap("data.pool-settings.properties", ImmutableMap.of()));

        return new StorageCredentials(
                c.getString("data.address", null),
                c.getString("data.database", null),
                c.getString("data.username", null),
                c.getString("data.password", null),
                maxPoolSize, minIdle, maxLifetime, connectionTimeout, props
        );
    }));

    /**
     * The prefix for any SQL tables
     */
    public static final ConfigKey<String> SQL_TABLE_PREFIX = enduringKey(customKey(
            c -> c.getString("data.table-prefix", c.getString("data.table_prefix", "pdsync_"))));


    /**
     * The name of the storage method being used
     */
    public static final ConfigKey<StorageType> STORAGE_METHOD = enduringKey(customKey(
            c -> StorageType.parse(c.getString("storage-method", "postgresql"), StorageType.POSTGRESQL)));


    private static final List<ConfigKeyTypes.BaseConfigKey<?>> KEYS;

    static {
        // get a list of all keys
        KEYS = Arrays.stream(ConfigKeys.class.getFields())
                .filter(f -> Modifier.isStatic(f.getModifiers()))
                .filter(f -> ConfigKey.class.equals(f.getType()))
                .map(f -> {
                    try {
                        return (ConfigKeyTypes.BaseConfigKey<?>) f.get(null);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(ImmutableCollectors.toList());

        // set ordinal values
        for (int i = 0; i < KEYS.size(); i++) {
            KEYS.get(i).ordinal = i;
        }
    }

    /**
     * Gets a list of the keys defined in this class.
     *
     * @return the defined keys
     */
    public static List<? extends ConfigKey<?>> getKeys() {
        return KEYS;
    }

}
