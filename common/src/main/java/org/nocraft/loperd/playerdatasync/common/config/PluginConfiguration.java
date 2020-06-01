package org.nocraft.loperd.playerdatasync.common.config;

import org.nocraft.loperd.playerdatasync.common.config.Adapter.ConfigurationAdapter;
import org.nocraft.loperd.playerdatasync.common.plugin.DataSyncPlugin;

/**
 * An abstract implementation of {@link Configuration}.
 *
 * <p>Values are loaded into memory on init.</p>
 */
public class PluginConfiguration implements Configuration {

    /**
     * The configurations loaded values.
     *
     * <p>The value corresponding to each key is stored at the index defined
     * by {@link ConfigKey#ordinal()}.</p>
     */
    private Object[] values = null;

    private final DataSyncPlugin plugin;
    private final ConfigurationAdapter adapter;

    public PluginConfiguration(DataSyncPlugin plugin, ConfigurationAdapter adapter) {
        this.plugin = plugin;
        this.adapter = adapter;

        load();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(ConfigKey<T> key) {
        return (T) this.values[key.ordinal()];
    }

    @Override
    public synchronized void load() {
        // if this is a reload operation
        boolean reload = true;

        // if values are null, must be loading for the first time
        if (this.values == null) {
            this.values = new Object[ConfigKeys.getKeys().size()];
            reload = false;
        }

        for (ConfigKey<?> key : ConfigKeys.getKeys()) {
            // don't reload enduring keys.
            if (reload && key instanceof ConfigKeyTypes.EnduringKey) {
                continue;
            }

            // load the value for the key
            Object value = key.get(this.adapter);
            this.values[key.ordinal()] = value;
        }
    }

    @Override
    public void reload() {
        this.adapter.reload();
        load();
    }

    @Override
    public DataSyncPlugin getPlugin() {
        return this.plugin;
    }
}
