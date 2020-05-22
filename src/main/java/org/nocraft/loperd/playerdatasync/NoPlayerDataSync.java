package org.nocraft.loperd.playerdatasync;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.nocraft.loperd.playerdatasync.Domain.Composer;
import org.nocraft.loperd.playerdatasync.Domain.Config.Adapter.ConfigurationAdapter;
import org.nocraft.loperd.playerdatasync.Domain.Config.PluginConfiguration;
import org.nocraft.loperd.playerdatasync.Domain.Scheduler.SchedulerAdapter;
import org.nocraft.loperd.playerdatasync.Listener.LockedPlayerListener;
import org.nocraft.loperd.playerdatasync.Listener.NoListener;
import org.nocraft.loperd.playerdatasync.Listener.PlayerLoadListener;
import org.nocraft.loperd.playerdatasync.Manager.LockedPlayerManager;
import org.nocraft.loperd.playerdatasync.Storage.Storage;
import org.nocraft.loperd.playerdatasync.Storage.StorageFactory;

import java.io.File;
import java.io.InputStream;

public final class NoPlayerDataSync extends JavaPlugin {

    private final Composer<NoListener> listeners = new Composer<>();
    @Getter
    private PlayerInventorySerializer playerInventorySerializer;
    @Getter
    private PluginConfiguration configuration;
    @Getter
    private SchedulerAdapter scheduler;
    private Storage storage;

    @Override
    public void onEnable() {
        LockedPlayerManager lockedManager = new LockedPlayerManager(this);

        // load configuration
        getLogger().info("Loading configuration...");

        this.configuration = new PluginConfiguration(this, provideConfigurationAdapter());
        this.storage = new StorageFactory(this).getInstance();
        this.scheduler = new BukkitSchedulerAdapter(this);

        this.listeners.add(new PlayerLoadListener(this, storage, lockedManager));
        this.listeners.add(new LockedPlayerListener(this, lockedManager));

        this.listeners.register();

        this.playerInventorySerializer = new PlayerInventorySerializer(new Bs64InventorySerializer());
    }

    @Override
    public void onDisable() {
        listeners.unregister();
        listeners.shutdown();
    }

    public InputStream getResourceStream(String path) {
        return getResource(path);
    }

    public String identifyClassLoader(ClassLoader loader) {
        if (loader instanceof org.bukkit.plugin.java.PluginClassLoader) {
            return ((org.bukkit.plugin.java.PluginClassLoader) loader).getPlugin().getName();
        }
        return null;
    }

    protected ConfigurationAdapter provideConfigurationAdapter() {
        return new BukkitConfigAdapter(this, resolveConfig());
    }

    private File resolveConfig() {
        File configFile = new File(this.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            this.getDataFolder().mkdirs();
            this.saveResource("config.yml", false);
        }
        return configFile;
    }
}
