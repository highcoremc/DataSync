package org.nocraft.loperd.playerdatasync.common.config;

import org.nocraft.loperd.playerdatasync.common.plugin.PDSyncPlugin;

/**
 * The master configuration.
 */
public interface Configuration {

    /**
     * Gets the main plugin instance.
     *
     * @return the plugin instance
     */
    PDSyncPlugin getPlugin();

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
