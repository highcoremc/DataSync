package org.nocraft.loperd.datasync.common.config;

import org.nocraft.loperd.datasync.common.plugin.DataSyncPlugin;

/**
 * The master configuration.
 */
public interface Configuration {

    /**
     * Gets the main plugin instance.
     *
     * @return the plugin instance
     */
    DataSyncPlugin getPlugin();

    /**
     * Reloads the configuration.
     */
    void reload();

    /**
     * Loads all configuration values.
     */
    void load();

    /**
     * Gets the value of a given context key.
     *
     * @param key the key
     * @param <T> the key return type
     * @return the value mapped to the given key. May be null.
     */
    <T> T get(ConfigKey<T> key);
}
